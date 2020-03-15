package nil.ed.livechat.common.repository.example;

import nil.ed.livechat.common.entity.RoomSubscriptionEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-09
 *
 * @author lidelin
 */

public class SubscriptionUserIdQueryExample extends Example {
    public SubscriptionUserIdQueryExample(Long userId) {
        super(RoomSubscriptionEntity.class);
        this.createCriteria().andEqualTo("uid", userId);
    }
}
