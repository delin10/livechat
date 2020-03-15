package nil.ed.livechat.login.interceptor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.login.security.AuthenticationConstants;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author lidelin
 */
@Slf4j
public class CustomSecuritySetFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("user = {}", user);
        request.setAttribute(AuthenticationConstants.USER_ATTRIBUTE, user);
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}