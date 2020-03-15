package nil.ed.livechat.web.config;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.enums.NginxNotificationType;
import nil.ed.livechat.login.interceptor.CustomSecuritySetFilter;
import nil.ed.livechat.login.security.CustomAuthenticationFailureHandler;
import nil.ed.livechat.login.security.CustomAuthenticationSuccessHandler;
import nil.ed.livechat.login.security.CustomPasswordEncoder;
import nil.ed.livechat.login.security.UserDetailServiceImpl;
import nil.ed.livechat.login.session.RedisSessionRegistry;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Created at 2020-03-07
 *
 * @author lidelin
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig<S extends Session> extends WebSecurityConfigurerAdapter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public RedisSessionRegistry sessionRegistry() {
        return new RedisSessionRegistry(stringRedisTemplate);
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailServiceImpl();
    }

    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter(RedisSessionRegistry sessionRegistry) {
        return new ConcurrentSessionFilter(sessionRegistry);
    }

    @Bean
    public CustomPasswordEncoder customPasswordEncoder(){
        return new CustomPasswordEncoder();
    }

    @Bean
    public CustomSecuritySetFilter customSecuritySetFilter() {
        return new CustomSecuritySetFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(customPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        MIME类型不匹配阻止静态资源
         */
        http.headers().contentTypeOptions().disable();
        http.addFilterAfter(customSecuritySetFilter(), FilterSecurityInterceptor.class);
        http.authorizeRequests()
                .antMatchers("/chatroom/login.html",
                        "/chatroom/register.html",
                        "/livechat/user/register",
                        "/chatroom/**/*.js",
                        "/chatroom/image/**",
                        "/chatroom/**/*.ico",
                        "/chatroom/**/*.css",
                        "/live/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/chatroom/login.html")
                .loginProcessingUrl("/chatroom/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
                .permitAll()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry());
    }
}
