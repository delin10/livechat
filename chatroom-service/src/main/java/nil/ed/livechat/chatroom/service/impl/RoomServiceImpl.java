package nil.ed.livechat.chatroom.service.impl;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.common.NormalResponseBuilder;
import nil.ed.livechat.chatroom.common.PageResult;
import nil.ed.livechat.chatroom.common.RedisPrefix;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.entity.RoomEntity;
import nil.ed.livechat.chatroom.mapper.RoomMapper;
import nil.ed.livechat.chatroom.service.AbstractService;
import nil.ed.livechat.chatroom.service.IRoomService;
import nil.ed.livechat.chatroom.service.MessageIncompleteException;
import nil.ed.livechat.chatroom.service.support.impl.SimpleDeleteHelper;
import nil.ed.livechat.chatroom.service.support.impl.SimpleSelectOneHelper;
import nil.ed.livechat.chatroom.service.support.impl.SimpleSelectPageHelper;
import nil.ed.livechat.chatroom.service.support.impl.SimpleUpdateHelper;
import nil.ed.livechat.chatroom.util.Checker;
import nil.ed.livechat.chatroom.vo.UserChatRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@Slf4j
@Service("roomService")
public class RoomServiceImpl extends AbstractService implements IRoomService {

    private static final int MAX_CHAT_COUNT_CONTINUOUSLY= 5;

    private static final long CHAT_COUNT_CONTINUOUSLY_INTERVAL_SEC = 5;

    @Resource(name = "roomMapper")
    private RoomMapper roomMapper;

    private ZSetOperations<String, Object> zSetOperations;

    private ValueOperations<String, Object> valueOperations;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Executor databaseQueryExecutor;

    @Autowired
    public void setDatabaseQueryExecutor(@Qualifier("databaseQueryExecutor") Executor databaseQueryExecutor) {
        this.databaseQueryExecutor = databaseQueryExecutor;
    }

    @Autowired
    public void setValueOperations(RedisTemplate<String, Object> objectRedisTemplate) {
        this.zSetOperations = objectRedisTemplate.opsForZSet();
        this.valueOperations = objectRedisTemplate.opsForValue();
    }

    @Override
    public Response<Void> addRoom(RoomEntity room) {
        room.setSecretKey(generateSecretKey());
        roomMapper.insert(room);
        return new NormalResponseBuilder<Void>()
                .success(null);
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
        final int start = 0, size = Integer.MAX_VALUE;
        return new SimpleSelectPageHelper<RoomEntity>(databaseQueryExecutor)
                .setPageNo(start)
                .setPageSize(size)
                .setCounter(() -> roomMapper.selectCount(new RoomEntity()))
                .operate(() -> roomMapper.selectAll());
    }

    @Override
    public Response<List<Object>> listRankUsersTop50(Long roomId) {
        final String key = String.format(RedisPrefix.ROOM_CHAT_COUNT_OF_USER_PATTERN, roomId);
        return new SimpleSelectOneHelper<List<Object>>()
                .operate(() -> unwrapTypedTuple(zSetOperations.reverseRangeWithScores(key, 0, 50)));
    }

    @Override
    public Response<Void> updateRoom(RoomEntity entity) {
        return new SimpleUpdateHelper().operate(() -> {
            entity.setSecretKey(null);
            entity.setCreateTime(null);
            entity.setDeleteFlag(null);
            entity.setUpdateTime(null);
            return roomMapper.updateByPrimaryKeySelective(entity);
        });
    }

    @Override
    public Response<RoomEntity> getRoomById(Long id) {
        return new SimpleSelectOneHelper<RoomEntity>()
                .operate(() -> roomMapper.selectByPrimaryKey(id));
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
    public boolean incrementChatRecord(Long roomId, Long userId, Long delta) {
        if (isLock(roomId, userId)) {
            return false;
        }
        int frequency = incrFrequency(roomId, userId);
        if (frequency > 5) {
            lock(roomId, userId);
            return false;
        }

        final String key = String.format(RedisPrefix.ROOM_CHAT_COUNT_OF_USER_PATTERN, roomId);
        zSetOperations.incrementScore(key, userId, delta);
        valueOperations.increment(String.format(RedisPrefix.ROOM_USER_CHAT_TOTAL_COUNT, roomId));

        return true;
    }

    @Override
    public Response<Integer> getRoomActivation(Long roomId) {
        return new SimpleSelectOneHelper<Integer>()
                .operate(() ->
                        Optional.ofNullable(valueOperations.increment(String.format(RedisPrefix.ROOM_USER_CHAT_TOTAL_COUNT, roomId), 0))
                                .orElse(0L).intValue());
    }

    @Override
    public boolean checkExist(Long id) {
        return roomMapper.existsWithPrimaryKey(id);
    }

    public int incrFrequency(Long roomId, Long userId) {
        String key = String.format(RedisPrefix.ROOM_USER_CHAT_FREQUENCY, roomId, userId);
        return incrAndExpireSecond(key, CHAT_COUNT_CONTINUOUSLY_INTERVAL_SEC);
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

    private void lock(Long roomId, Long userId) {
        valueOperations.set(String.format(RedisPrefix.ROOM_USER_CHAT_LOCK, roomId, userId), 1, 30, TimeUnit.SECONDS);
    }

    private boolean isLock(Long roomId, Long userId) {
       return valueOperations.get(String.format(RedisPrefix.ROOM_USER_CHAT_LOCK, roomId, userId)) != null;
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
}
