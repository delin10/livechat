package nil.ed.livechat.chatroom.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nil.ed.livechat.login.security.AuthenticationConstants;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created at 2020-01-13
 *
 * @author lidelin
 */

public class UserCheckInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        CustomUserDetailsImpl user = (CustomUserDetailsImpl) request.getAttribute(AuthenticationConstants.USER_ATTRIBUTE);
        request.setAttribute("uid", user.getId());
        return true;
    }

}
