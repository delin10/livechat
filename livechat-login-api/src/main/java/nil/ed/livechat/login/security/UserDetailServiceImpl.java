package nil.ed.livechat.login.security;

import javax.annotation.Resource;

import java.text.MessageFormat;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.aop.annotation.MethodInvokeLog;
import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.common.repository.UserRepository;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 用户权限获取服务
 * @author lidelin
 * @since 2019-08-29
 */
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private UserRepository userRepository;

    @Override
    @MethodInvokeLog
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserEntity user = userRepository.getByUsername(username);

            if (user != null){
                if (log.isDebugEnabled()) {
                    log.debug("Get user = {}", user);
                }
                return new CustomUserDetailsImpl(user.getId(), user.getUsername(), user.getPwd());
            }else{
                throw new UsernameNotFoundException(MessageFormat.format("No such user whose username = {0}", username));
            }
        }catch (Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
