package nil.ed.livechat.common.repository.example;

import nil.ed.livechat.common.entity.RoomSubscriptionEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-08
 *
 * @author lidelin
 */

public class SubscriptionKeyQueryExample extends Example {
    public SubscriptionKeyQueryExample(Long roomId, Long uid) {
        super(RoomSubscriptionEntity.class);
        this.createCriteria().andEqualTo("roomId", roomId).andEqualTo("uid", uid);
    }
}
