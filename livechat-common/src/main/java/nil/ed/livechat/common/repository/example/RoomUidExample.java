package nil.ed.livechat.common.repository.example;

import nil.ed.livechat.common.entity.RoomEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-10
 *
 * @author lidelin
 */

public class RoomUidExample extends Example {
    public RoomUidExample(Long uid) {
        super(RoomEntity.class);
        this.createCriteria().andEqualTo("uid", uid);
    }
}
