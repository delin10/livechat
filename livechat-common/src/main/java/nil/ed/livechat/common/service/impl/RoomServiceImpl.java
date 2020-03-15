package nil.ed.livechat.common.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.aop.annotation.MethodInvokeLog;
import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.PageResult;
import nil.ed.livechat.common.common.RedisPrefix;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeMessage;
import nil.ed.livechat.common.common.RoomStatusEnum;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.entity.RoomSubscriptionEntity;
import nil.ed.livechat.common.entity.RoomTagEntity;
import nil.ed.livechat.common.entity.TagEntity;
import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.common.mapper.RoomMapper;
import nil.ed.livechat.common.mapper.RoomTagMapper;
import nil.ed.livechat.common.repository.RoomRepository;
import nil.ed.livechat.common.repository.RoomSubscriptionRepository;
import nil.ed.livechat.common.repository.RoomTagRepository;
import nil.ed.livechat.common.repository.TagRepository;
import nil.ed.livechat.common.repository.UserRepository;
import nil.ed.livechat.common.service.IRoomService;
import nil.ed.livechat.common.service.ITagService;
import nil.ed.livechat.common.service.IUserService;
import nil.ed.livechat.common.service.MessageIncompleteException;
import nil.ed.livechat.common.service.support.impl.SimpleDeleteHelper;
import nil.ed.livechat.common.service.support.impl.SimpleInsertHelper;
import nil.ed.livechat.common.service.support.impl.SimpleSelectOneHelper;
import nil.ed.livechat.common.service.support.impl.SimpleSelectPageHelper;
import nil.ed.livechat.common.service.support.impl.SimpleUpdateHelper;
import nil.ed.livechat.common.util.Checker;
import nil.ed.livechat.common.util.GroupUtils;
import nil.ed.livechat.common.util.redis.SynchronizedRedisCache;
import nil.ed.livechat.common.vo.IndexRoomListVO;
import nil.ed.livechat.common.vo.OwnedRoomVO;
import nil.ed.livechat.common.vo.SubscribedRoomVO;
import nil.ed.livechat.common.vo.TagVO;
import nil.ed.livechat.common.vo.UserChatRecordVO;
import nil.ed.livechat.common.vo.UserBaseVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@Slf4j
@Service("roomService")
public class RoomServiceImpl implements IRoomService {

    private static final long FREQUENCY_CONTROL_LOCK_TIME_S = 30;

    private static final long FREQUENCY_CONTROL_FREQUENCY = 5;

    private static final long FREQUENCY_CONTROL_TIME_S = 5;

    private static final int MAX_RELATIVE_ROOM_COUNT = 20;

    private static final int SCAN_COUNT = 20;

    private static final long MAX_UPDATE_IDLE_TIME_MINUTE = 5;

    @Resource(name = "roomMapper")
    private RoomMapper roomMapper;

    private ZSetOperations<String, Object> zSetOperations;

    private ValueOperations<String, Object> valueOperations;

    private HashOperations<String, Long, Object> hashOperations;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RoomTagMapper roomTagMapper;

    @Resource
    private RoomSubscriptionRepository roomSubscriptionRepository;

    @Resource
    private RoomTagRepository roomTagRepository;

    @Resource
    private RoomRepository roomRepository;

    @Resource
    private IUserService userService;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TagRepository tagRepository;

    @Resource
    private ITagService tagService;

    private Executor databaseQueryExecutor;

    private SynchronizedRedisCache<Map<Long, Double>> activationCache;

    @PostConstruct
    public void init() {
        activationCache = new SynchronizedRedisCache<>(key -> {
            Cursor<ZSetOperations.TypedTuple<Object>> cursor = zSetOperations.scan(key, ScanOptions.scanOptions().count(SCAN_COUNT).build());
            Map<Long, Double> map = new TreeMap<>();
            while (cursor.hasNext()) {
                ZSetOperations.TypedTuple<Object> tuple = cursor.next();
                map.put((Long)tuple.getValue(), tuple.getScore());
            }
            return map;
        }, (oldKey, key) -> {
            Map<Long, Double> activationMap = listAllOnlineRoomsActivation();
            activationMap.forEach((k, v) -> zSetOperations.add(key, k, v));
        }, RedisPrefix.ROOM_RANK_LIST.getRawKey());
        activationCache.setUpdateSuccess((oldKey, key) -> zSetOperations.getOperations().unlink(oldKey));
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void refreshCache() {
        log.info("Start to refresh cache...");
        activationCache.update();
        log.info("Succeed to update!");
    }

    @Autowired
    public void setDatabaseQueryExecutor(@Qualifier("databaseQueryExecutor") Executor databaseQueryExecutor) {
        this.databaseQueryExecutor = databaseQueryExecutor;
    }

    @Autowired
    public void setValueOperations(RedisTemplate<String, Object> objectRedisTemplate) {
        this.zSetOperations = objectRedisTemplate.opsForZSet();
        this.valueOperations = objectRedisTemplate.opsForValue();
        this.hashOperations = objectRedisTemplate.opsForHash();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> addRoom(RoomEntity room, Set<Long> tags) {
        if (!userService.isVerified(room.getUid())) {
            return new NormalResponseBuilder<Void>()
                    .setCodeMessage(ResponseCodeMessage.FAILED)
                    .build();
        }
        return new SimpleInsertHelper()
                .operate(() -> {
                    room.setSecretKey(generateSecretKey());
                    roomRepository.addRoom(room);
                    tags.stream()
                            .filter(tagRepository::checkExists)
                            .forEach(t -> {
                                RoomTagEntity e = new RoomTagEntity();
                                e.setRoomId(room.getId());
                                e.setTagId(t);
                                roomTagRepository.insert(e);
                            });
                    return null;
                });
    }

    @Override
    public Response<Void> deleteRoom(Long id, Long operator) {
        return new SimpleDeleteHelper()
                .operate(() -> {
                    Example example = Example
                            .builder(RoomEntity.class)
                            .build();
                    example.createCriteria()
                    .andEqualTo("id", id)
                    .andEqualTo("creator", operator);
                    return roomMapper.deleteByExample(example);
                });
    }

    @Override
    public Response<PageResult<RoomEntity>> listRooms() {
        final int start = 0 ;
        return new SimpleSelectPageHelper<RoomEntity>(databaseQueryExecutor)
                .setPageNo(start)
                .setPageSize(Integer.MAX_VALUE)
                .setCounter(() -> roomMapper.selectCount(new RoomEntity()))
                .operate(() -> roomMapper.selectAll());
    }

    @Override
    public Response<List<Object>> listRankUsersTop50(Long roomId) {
        final String key = RedisPrefix.ROOM_MESSAGE_COUNT.getKeyWithTemplate(roomId);
        return new SimpleSelectOneHelper<List<Object>>()
                .operate(() -> unwrapTypedTuple(zSetOperations.reverseRangeWithScores(key, 0, 50)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> updateRoom(RoomEntity entity, Set<Long> tags) {
        return new SimpleUpdateHelper().operate(() -> {
            Response<List<TagVO>> resp = tagService.listTagsByRoomId(entity.getId());
            if (resp.getCode() != ResponseCodeMessage.SUCCESS.getCode()) {
                return 0;
            }
            Set<Long> curIdSet = resp.getData().stream()
                    .map(TagVO::getId)
                    .collect(Collectors.toSet());
            Map<GroupUtils.GroupType, List<Long>> map = GroupUtils.groupNoCopy(curIdSet, tags);
            roomTagRepository.deleteAll(map.get(GroupUtils.GroupType.ONLY_OLD));
            Optional.ofNullable(map.get(GroupUtils.GroupType.ONLY_NEW)).ifPresent(adds -> {
                adds.forEach(i -> {
                    RoomTagEntity e = new RoomTagEntity();
                    e.setRoomId(entity.getId());
                    e.setTagId(i);
                    roomTagRepository.insert(e);
                });
            });
            return roomRepository.updateByIdAndUid(entity);
        });
    }

    @Override
    @MethodInvokeLog
    public Response<SubscribedRoomVO> getRoomById(Long roomId, Long userId) {
        return new SimpleSelectOneHelper<SubscribedRoomVO>()
                .operate(() -> {
                    SubscribedRoomVO subscribedRoomVO = null;
                    RoomEntity roomEntity = roomMapper.selectByPrimaryKey(roomId);
                    if (roomEntity != null) {
                        subscribedRoomVO = toSubscribedRoomVO(roomEntity);
                    }
                    return subscribedRoomVO;
                });
    }

    @Override
    public Response<RoomEntity> getBySecretKey(String secretKey) {
        return new SimpleSelectOneHelper<RoomEntity>()
                .operate(() -> {
                    Example example = Example
                            .builder(RoomEntity.class)
                            .build();
                    example.createCriteria()
                            .andEqualTo("secretKey", secretKey);
                    return roomMapper.selectOneByExample(example);
                });
    }

    @Override
    @MethodInvokeLog
    public boolean incrementChatRecord(Long roomId, Long userId, Long delta) {
        if (isLock(roomId, userId)) {
            return false;
        }
        frequencyControl(roomId, userId);
        if (isLock(roomId, userId)) {
            return false;
        }
        final String roomMsgCountKey = RedisPrefix.ROOM_MESSAGE_COUNT.getKeyWithTemplate(roomId);
        valueOperations.increment(roomMsgCountKey);
        hashOperations.increment(RedisPrefix.ROOM_AUDIENCE_MAP.getKeyWithTemplate(roomId), userId, 1);
        return true;
    }

    @Override
    @MethodInvokeLog
    public Response<Double> getRoomActivation(Long roomId) {
        return new SimpleSelectOneHelper<Double>()
                .operate(() -> calActivation(roomId));
    }

    private double calActivation(Long roomId) {
        String roomMsgCountKey = RedisPrefix.ROOM_MESSAGE_COUNT.getKeyWithTemplate(roomId);
        String roomAudienceMapKey = RedisPrefix.ROOM_AUDIENCE_MAP.getKeyWithTemplate(roomId);
        Long userCount = hashOperations.size(roomAudienceMapKey);
        Long msgCount = Optional.ofNullable(valueOperations.increment(roomMsgCountKey, 0)).orElse(Long.MIN_VALUE);
        return userCount * 0.7 + msgCount * 0.3;
    }

    @Override
    public boolean checkExist(Long id) {
        return roomMapper.existsWithPrimaryKey(id);
    }

    public void frequencyControl(Long roomId, Long userId) {
        long nowSec = Instant.now().getEpochSecond();
        String key = RedisPrefix.ROOM_FREQUENCY_CONTROL_RECORD.getKeyWithTemplate(roomId, userId);
        zSetOperations.add(key, System.currentTimeMillis(), nowSec);
        zSetOperations.removeRangeByScore(key, 0.0D, nowSec - FREQUENCY_CONTROL_TIME_S + 0.0D);
        if (redisZSetSize(key) > FREQUENCY_CONTROL_FREQUENCY) {
            lock(roomId, userId);
        }
    }

    private long redisZSetSize(String key) {
        return Optional.ofNullable(zSetOperations.zCard(key)).orElse(0L);
    }

    public int incrAndExpireSecond(String key, Long timeout) {
        Long v = valueOperations.increment(key);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        Assert.notNull(v, "v cannot be null");
        return v.intValue();

    }

    @Override
    public boolean validateRoomId(Message<?> message, String roomIdStr) {
        log.info("Start to validateRoomId, roomId={}", roomIdStr);
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "StompHeaderAccessor not null");

        Number roomIdNumber = null;
        if (roomIdStr != null){
            roomIdNumber = Checker.checkPureLongNumber(roomIdStr);
        }

        return !(roomIdNumber == null
                || !checkExist(roomIdNumber.longValue()));
    }

    @Override
    public String resolveRoomId(StompHeaderAccessor accessor) {
        String roomIdStr = Optional.ofNullable(accessor.getNativeHeader("room-id"))
                .filter(ls -> !ls.isEmpty())
                .map(ls -> ls.get(0))
                .orElseGet(() -> {
                    String destination = accessor.getFirstNativeHeader(SimpMessageHeaderAccessor.DESTINATION_HEADER);
                    if (destination == null){
                        throw new MessageIncompleteException("Invalid header: loss destination");
                    }
                    int dotLastIndex = Optional.of(destination.lastIndexOf('.'))
                            .filter(index -> index > 0)
                            .orElseThrow(() -> new MessageIncompleteException("Invalid header: there is no room id in destination!"));

                    String roomStr = destination.substring(dotLastIndex);
                    accessor.setNativeHeader("room-id", roomStr);
                    return roomStr;
                });
        log.info("resolved room id: {}", roomIdStr);
        return roomIdStr;
    }

    @Override
    public Response<List<SubscribedRoomVO>> listRelativeRooms(Long roomId) {
        return new SimpleSelectOneHelper<List<SubscribedRoomVO>>().operate(() -> {
            Example example = new Example(RoomTagEntity.class);
            example.createCriteria().andEqualTo("roomId", roomId);
            List<RoomTagEntity> roomTagEntityList = roomTagMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(roomTagEntityList)) {
                return Collections.emptyList();
            }

            Map<Long, Integer> similarityStatistic = new HashMap<>(roomTagEntityList.size());

            for (RoomTagEntity roomTagEntity : roomTagEntityList) {
                Long tagId = roomTagEntity.getTagId();
                List<RoomTagEntity> roomTagEntities = roomTagRepository.listByTagId(tagId);
                roomTagEntities.forEach(entity -> {
                    similarityStatistic.putIfAbsent(entity.getRoomId(), 0);
                    similarityStatistic.computeIfPresent(entity.getRoomId(), (k, v) -> v + 1);
                });
            }


            Long[] roomIdArr = similarityStatistic.keySet().toArray(new Long[0]);
            Arrays.sort(roomIdArr);
            return Arrays.stream(roomIdArr).limit(MAX_RELATIVE_ROOM_COUNT).map(this::getRoomVO).collect(Collectors.toList());
        });

    }

    private void lock(Long roomId, Long userId) {
        valueOperations.setIfAbsent(RedisPrefix.ROOM_FREQUENCY_CONTROL_LOCK.getKeyWithTemplate(roomId, userId), 1, FREQUENCY_CONTROL_LOCK_TIME_S, TimeUnit.SECONDS);
    }

    private boolean isLock(Long roomId, Long userId) {
        Object v = valueOperations.get(RedisPrefix.ROOM_FREQUENCY_CONTROL_LOCK.getKeyWithTemplate(roomId, userId));
       return v != null && !v.equals(0);
    }

    private List<Object> unwrapTypedTuple(Set<ZSetOperations.TypedTuple<Object>> typedTuples){
        return Optional.ofNullable(typedTuples)
                .map(set -> set.stream()
                        .map(this::mapToUserChatRecordVO)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private Object mapToUserChatRecordVO(ZSetOperations.TypedTuple<Object> typedTuple){
        UserChatRecordVO userChatRecordVO = new UserChatRecordVO();
        userChatRecordVO.setUsername(typedTuple.getValue()+"");
        userChatRecordVO.setChatCount(Optional.ofNullable(typedTuple.getScore()).map(Double::longValue).orElse(0L));
        return userChatRecordVO;
    }

    private String generateSecretKey() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 24);
    }

    private SubscribedRoomVO getRoomVO(Long id) {
        RoomEntity roomEntity = roomMapper.selectByPrimaryKey(id);

        if (roomEntity == null) {
            return null;
        }
        return toSubscribedRoomVO(roomEntity);
    }

    @Override
    public Response<IndexRoomListVO> listOnlineRoomsOrderByActivation(Integer pageNum, Integer size) {
        return new SimpleSelectOneHelper<IndexRoomListVO>()
                .operate(() -> helpListOnlineRoomsOrderByActivation(pageNum, size));
    }

    private Map<Long, Double> listAllOnlineRoomsActivation() {
        Set<RoomStatusEnum> statusSet = Sets.newHashSet(RoomStatusEnum.ONLINE);
        List<RoomEntity> ls =
                roomRepository.listRoomsByStatus(statusSet);
        /*
        排序
         */
        Map<Long, Double> activationMap = new HashMap<>(ls.size());
        ls.forEach(entity -> activationMap.put(entity.getId(), getRoomActivation(entity.getId()).getData()));
        return activationMap;
    }

    private IndexRoomListVO helpListOnlineRoomsOrderByActivation(Integer pageNum, Integer size) {
        Map<Long, Double> map = activationCache.query();
        List<Long> ls = Lists.newLinkedList(map.keySet());
        /*
        分页
         */
        int start = pageNum * size;
        List<Long> subList = Collections.emptyList();
        if (start < ls.size()) {
            int end = Math.min(size + start, ls.size());
            subList = ls.subList(start, end);
        }
        List<SubscribedRoomVO> subscribedRoomVOList = subList.parallelStream().map(this::getRoomVO).collect(Collectors.toList());
        int count = ls.size();
        IndexRoomListVO indexRoomListVO = new IndexRoomListVO();
        indexRoomListVO.setCount(count);
        indexRoomListVO.setList(subscribedRoomVOList);
        return indexRoomListVO;
    }

    private boolean isOnline(Long roomId) {
        return roomRepository.getRoomById(roomId).getStatus().intValue() == RoomStatusEnum.ONLINE.getCode();
    }

    @Override
    @MethodInvokeLog
    public Response<List<SubscribedRoomVO>> recommendRooms(Long roomId, Long userId, Integer count) {
        return new SimpleSelectOneHelper<List<SubscribedRoomVO>>()
                .operate(() -> helpRecommendation(roomId, userId, count));
    }

    private List<SubscribedRoomVO> helpRecommendation(Long roomId, Long userId, Integer count) {
        Set<Long> distinctRoomSet = new HashSet<>(count + 1);
        Set<Long> distinctTagSet = new HashSet<>();
        distinctRoomSet.add(roomId);
        List<Long> roomList = new LinkedList<>(helpRecommendByRoomId(roomId, distinctTagSet, distinctRoomSet, count));
        if (roomList.size() < count) {
            List<RoomSubscriptionEntity> subscriptions = roomSubscriptionRepository.listByUserId(userId);
            Set<Long> availSet = subscriptions.stream()
                    .map(RoomSubscriptionEntity::getRoomId)
                    .filter(distinctRoomSet::add)
                    .collect(Collectors.toSet());
            for (Long rid : availSet) {
                roomList.addAll(helpRecommendByRoomId(rid, distinctTagSet, distinctRoomSet, count));
                if (roomList.size() >= count) {
                    break;
                }
            }

        }

        if (roomList.size() < count) {
            Map<Long, Double> map = activationCache.query();
            for (Map.Entry<Long, Double> entry : map.entrySet()) {
                roomList.addAll(helpRecommendByRoomId(entry.getKey(), distinctTagSet, distinctRoomSet, count));
            }
        }

        return roomList.parallelStream()
                .map(this::getRoomVO)
                .collect(Collectors.toList());
    }

    private Set<Long> helpRecommendByRoomId(Long roomId, Set<Long> tagExcludeSet, Set<Long> roomExcludeSet, int count) {
        List<RoomTagEntity> roomTagEntityList = roomTagRepository.listByRoomId(roomId);
        Set<Long> tagIdSet = roomTagEntityList.stream()
                .map(RoomTagEntity::getTagId)
                .collect(Collectors.toSet());
        Set<Long> candidateRoomIds = new HashSet<>(count);
        for (Long tagId : tagIdSet) {
            if (!tagExcludeSet.add(tagId)) {
                continue;
            }

            List<RoomTagEntity> tagEntities = roomTagRepository.listByTagId(tagId);
            for (RoomTagEntity tag : tagEntities) {
                Long candidateRoomId = tag.getRoomId();
                if (roomExcludeSet.add(candidateRoomId)) {
                    if (isOnline(candidateRoomId)) {
                        candidateRoomIds.add(candidateRoomId);
                    } else {
                        roomExcludeSet.add(candidateRoomId);
                    }
                }
                if (candidateRoomIds.size() >= count) {
                    break;
                }
            }
            if (candidateRoomIds.size() >= count) {
                break;
            }
        }
        return candidateRoomIds;
    }

    @Override
    public Response<List<OwnedRoomVO>> listByUid(Long uid) {
        return new SimpleSelectOneHelper<List<OwnedRoomVO>>()
                .operate(() -> {
                    List<RoomEntity> roomList = roomRepository.listByUid(uid);

                    return roomList.parallelStream()
                            .map(OwnedRoomVO::parse)
                            .peek(r -> r.setTags(tagService.listTagsByRoomId(r.getId()).getData()))
                            .collect(Collectors.toList());
                });
    }

    private SubscribedRoomVO toSubscribedRoomVO(RoomEntity roomEntity) {
        Long roomId = roomEntity.getId();
        Long userId = roomEntity.getUid();
        UserEntity user = userRepository.getUserById(roomEntity.getUid());
        List<Long> tagIds = roomTagRepository.listByRoomId(roomId)
                .stream()
                .map(RoomTagEntity::getTagId)
                .collect(Collectors.toList());
        List<TagVO> tags = Collections.emptyList();
        if (!tagIds.isEmpty()) {
             tags = tagRepository.listTags(tagIds).stream().map(TagVO::parse).collect(
                    Collectors.toList());
        }
        SubscribedRoomVO subscribedRoomVO = SubscribedRoomVO.parse(roomEntity, UserBaseVO.parse(user), tags);
        RoomSubscriptionEntity roomSubscriptionEntity = roomSubscriptionRepository.getByUserIdAndRoomId(userId, roomId);
        subscribedRoomVO.setIsSubscribed(roomSubscriptionEntity != null);
        subscribedRoomVO.setActivation(getRoomActivation(roomId).getData());
        subscribedRoomVO.setSubscriptionCount(getSubscriptionCount(roomId).getData());
        return subscribedRoomVO;
    }

    @Override
    @MethodInvokeLog
    public Response<Void> updateRoomStatus(Long id, String secretKey, RoomStatusEnum status) {
        return new SimpleUpdateHelper()
                .operate(() -> {
                    RoomEntity entity = new RoomEntity();
                    entity.setId(id);
                    entity.setSecretKey(secretKey);
                    entity.setStatus(status.getCode());
                    return roomRepository.updateBySecretKeyAndId(entity);
                });
    }

    @Override
    @MethodInvokeLog
    public Response<Void> updateRoomStatus(Long id, RoomStatusEnum status) {
        return updateRoomStatus(id, null, status);
    }

    @Override
    @MethodInvokeLog
    public Response<Integer> getSubscriptionCount(Long roomId) {
        return new SimpleSelectOneHelper<Integer>()
                .operate(() -> roomSubscriptionRepository.countByRoomId(roomId));
    }

    @Override
    @MethodInvokeLog
    public Response<Void> subscribe(Long uid, Long roomId) {
        return new SimpleInsertHelper().operate(() -> {
            RoomSubscriptionEntity entity = new RoomSubscriptionEntity();
            entity.setRoomId(roomId);
            entity.setUid(uid);
            roomSubscriptionRepository.addRoomSubscription(entity);
            return null;
        });
    }

    @Override
    @MethodInvokeLog
    public Response<Void> unsubscribe(Long uid, Long roomId) {
        return new SimpleDeleteHelper().operate(() -> roomSubscriptionRepository.deleteRoomSubscription(uid, roomId));
    }

    @Override
    @MethodInvokeLog
    public void updateRoomChatOnlineCount(Long roomId, long delta) {
        valueOperations.increment(RedisPrefix.ROOM_CHAT_ONLINE_COUNT.getKeyWithTemplate(roomId), delta);
    }

    @Override
    @MethodInvokeLog
    public Long getRoomChatOnlineCount(Long roomId) {
        return valueOperations.increment(RedisPrefix.ROOM_CHAT_ONLINE_COUNT.getKeyWithTemplate(roomId), 0);
    }

    @Override
    @MethodInvokeLog
    public void updateLastUpdatePublishTimestamp(Long roomId) {
        String key = RedisPrefix.ROOM_LAST_PUB_TIMESTAMP.getKeyWithTemplate(roomId);
        valueOperations.set(key, Instant.now().getEpochSecond());
    }

    @Override
    @MethodInvokeLog
    public Long getLastUpdatePublishTimestamp(Long roomId) {
        String key = RedisPrefix.ROOM_LAST_PUB_TIMESTAMP.getKeyWithTemplate(roomId);
        return Optional.ofNullable(valueOperations.get(key))
                .map(v -> (Long) v)
                .orElse(-1L);
    }

    @Override
    @MethodInvokeLog
    public void cleanRoom(Long roomId) {
        Collection<String> keys = new LinkedList<>();
        keys.add(RedisPrefix.ROOM_AUDIENCE_MAP.getKeyWithTemplate(roomId));
        keys.add(RedisPrefix.ROOM_MESSAGE_COUNT.getKeyWithTemplate(roomId));
        keys.add(RedisPrefix.ROOM_CHAT_ONLINE_COUNT.getKeyWithTemplate(roomId));
        keys.add(RedisPrefix.ROOM_LAST_PUB_TIMESTAMP.getKeyWithTemplate(roomId));
        redisTemplate.delete(keys);
    }

    @Scheduled(cron = "0 5/5 * * * ?")
    @MethodInvokeLog
    public void scheduledCheckAndClean() {
        Map<Long, Double> roomList = activationCache.query();
        roomList.forEach((k, v) -> {
            Long ts = getLastUpdatePublishTimestamp(k);
            Long now = Instant.now().getEpochSecond();
            if (ts > 0 && TimeUnit.MINUTES.toMinutes(now - ts) > MAX_UPDATE_IDLE_TIME_MINUTE) {
                if (isOnline(k)) {
                    updateRoomStatus(k, RoomStatusEnum.OFFLINE);
                }
                cleanRoom(k);
            }
        });
    }
}
