package nil.ed.livechat.common.repository.example;

import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.entity.RoomSubscriptionEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-12
 *
 * @author lidelin
 */

public class RoomIdUidExample extends Example {
    public RoomIdUidExample(Long uid, Long roomId) {
        super(RoomEntity.class);
        this.createCriteria().andEqualTo("id", roomId).andEqualTo("uid", uid);
    }
}
