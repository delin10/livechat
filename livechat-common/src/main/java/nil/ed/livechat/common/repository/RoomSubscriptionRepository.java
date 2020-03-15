package nil.ed.livechat.common.repository;

import javax.annotation.Resource;

import java.util.List;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.entity.RoomSubscriptionEntity;
import nil.ed.livechat.common.mapper.RoomSubscriptionMapper;
import nil.ed.livechat.common.repository.example.SubscriptionKeyQueryExample;
import nil.ed.livechat.common.repository.example.SubscriptionRoomIdExample;
import nil.ed.livechat.common.repository.example.SubscriptionRoomIdUidExample;
import nil.ed.livechat.common.repository.example.SubscriptionUserIdQueryExample;
import org.springframework.stereotype.Repository;

/**
 * Created at 2020-03-08
 *
 * @author lidelin
 */

@Repository
public class RoomSubscriptionRepository {

    @Resource
    private RoomSubscriptionMapper roomSubscriptionMapper;

    public RoomSubscriptionEntity getByUserIdAndRoomId(Long userId, Long roomId) {
        return roomSubscriptionMapper.selectOneByExample(new SubscriptionKeyQueryExample(roomId, userId));
    }

    public List<RoomSubscriptionEntity> listByUserId(Long userId) {
        return roomSubscriptionMapper.selectByExample(new SubscriptionUserIdQueryExample(userId));
    }

    public int countByRoomId(Long roomId) {
        return roomSubscriptionMapper.selectCountByExample(new SubscriptionRoomIdExample(roomId));
    }

    public void addRoomSubscription(RoomSubscriptionEntity roomSubscriptionEntity) {
        roomSubscriptionMapper.insert(roomSubscriptionEntity);
    }

    public int deleteRoomSubscription(Long uid, Long roomId) {
        return roomSubscriptionMapper.deleteByExample(new SubscriptionRoomIdUidExample(uid, roomId));
    }

}
