package nil.ed.livechat.test.repository;

import javax.annotation.Resource;

import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.test.AbstractTest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import nil.ed.livechat.common.repository.UserRepository;

public class UserRepositoryTest extends AbstractTest {

    @Resource
    private UserRepository userRepository;

    @Test
    public void getByUsername() {
    }

    @Test
    public void addUser() {
        UserEntity entity = new UserEntity();
        entity.setUsername("lidelin");
        entity.setNickname("lidelin");
        entity.setTel("15219171826");
        entity.setPwd(DigestUtils.md5Hex("123456"));
        userRepository.addUser(entity);
    }
}