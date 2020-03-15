package nil.ed.livechat.common.repository.example;

import nil.ed.livechat.common.entity.UserEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-07
 *
 * @author lidelin
 */

public class UsernameExample  extends Example {

    public UsernameExample(String username) {
        super(UserEntity.class);
        this.createCriteria().andEqualTo("username", username);
    }

}
