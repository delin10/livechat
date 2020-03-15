package nil.ed.livechat.common.repository;

import javax.annotation.Resource;

import nil.ed.livechat.common.aop.annotation.MethodInvokeLog;
import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.common.mapper.UserMapper;
import nil.ed.livechat.common.repository.example.UsernameExample;
import org.springframework.stereotype.Repository;

/**
 * Created at 2020-03-07
 *
 * @author lidelin
 */

@Repository
public class UserRepository {

    @Resource
    private UserMapper userMapper;

    @MethodInvokeLog
    public UserEntity getByUsername(String username) {
        return userMapper.selectOneByExample(new UsernameExample(username));
    }

    @MethodInvokeLog
    public void addUser(UserEntity user) {
        userMapper.insert(user);
    }

    public boolean updateUserByIdIgnoredNull(UserEntity user) {
        return userMapper.updateByPrimaryKeySelective(user) > 0;
    }

    public UserEntity getUserById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
