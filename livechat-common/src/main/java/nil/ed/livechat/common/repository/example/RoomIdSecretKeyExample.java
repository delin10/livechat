package nil.ed.livechat.common.repository.example;

import nil.ed.livechat.common.entity.RoomEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-11
 *
 * @author lidelin
 */

public class RoomIdSecretKeyExample extends Example {

    public RoomIdSecretKeyExample(Long id, String secretKey) {
        super(RoomEntity.class);
        Criteria criteria  = this.createCriteria();
        if (id != null) {
            criteria.andEqualTo("id", id);
        }
        if (secretKey != null) {
            criteria.andEqualTo("secretKey", secretKey);
        }
    }
}
