package nil.ed.livechat.common.repository;

import javax.annotation.Resource;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.common.RoomStatusEnum;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.mapper.RoomMapper;
import nil.ed.livechat.common.repository.example.RoomIdSecretKeyExample;
import nil.ed.livechat.common.repository.example.RoomIdUidExample;
import nil.ed.livechat.common.repository.example.SubscriptionRoomIdUidExample;
import nil.ed.livechat.common.repository.example.RoomStatusExample;
import nil.ed.livechat.common.repository.example.RoomUidExample;
import org.springframework.stereotype.Repository;

/**
 * Created at 2020-02-10
 *
 * @author lidelin
 */
@Slf4j
@Repository
public class RoomRepository {

    @Resource
    private RoomMapper roomMapper;

    public RoomEntity getRoomById(Long id) {
        log.info("Start to invoke getRoomById with params: id = {}", id);
        try {
            RoomEntity roomEntity = roomMapper.selectByPrimaryKey(id);
            log.info("Succeed to invoke getRoomById with result={}", roomEntity);
            return roomEntity;
        } catch (Exception e) {
            log.error("Failed to invoke getRoomById! ", e);
            throw e;
        }
    }

    public List<RoomEntity> listRoomsByStatus(Set<RoomStatusEnum> status){
        return roomMapper.selectByExample(new RoomStatusExample(status));
    }

    public int countRoomsByStatus(Set<RoomStatusEnum> status){
        return roomMapper.selectCountByExample(new RoomStatusExample(status));
    }

    public List<RoomEntity> listByUid(Long uid) {
        return roomMapper.selectByExample(new RoomUidExample(uid));
    }

    public void addRoom(RoomEntity roomEntity) {
        roomMapper.insert(roomEntity);
    }

    public int updateByIdAndUid(RoomEntity entity) {
        return roomMapper.updateByExampleSelective(entity, new RoomIdUidExample(entity.getId(), entity.getUid()));
    }

    public int updateBySecretKeyAndId(RoomEntity entity) {
        return roomMapper.updateByExampleSelective(entity, new RoomIdSecretKeyExample(entity.getId(), entity.getSecretKey()));
    }

}
