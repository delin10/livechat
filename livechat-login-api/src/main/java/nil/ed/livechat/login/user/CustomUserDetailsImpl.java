package nil.ed.livechat.login.user;

import java.util.Collections;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

/**
 * Created at 2020-03-07
 *
 * @author lidelin
 */

@Getter
public class CustomUserDetailsImpl extends User {

    private Long id;


    public CustomUserDetailsImpl(Long id, String username, String password) {
        super(username, password, Collections.emptyList());
        this.id = id;
    }

}
