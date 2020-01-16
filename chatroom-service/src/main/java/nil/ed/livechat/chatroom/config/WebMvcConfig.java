package nil.ed.livechat.chatroom.config;

import nil.ed.livechat.chatroom.interceptor.UserCheckInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created at 2020-01-13
 *
 * @author lidelin
 */

public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserCheckInterceptor());
    }

}
