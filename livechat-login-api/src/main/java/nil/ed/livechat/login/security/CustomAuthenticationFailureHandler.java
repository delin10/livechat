package nil.ed.livechat.login.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeEnum;
import nil.ed.livechat.common.util.ExceptionUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws
            IOException, ServletException {
        log.info("Failed to authentication!");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        Response<String> res = new NormalResponseBuilder<String>()
                .setCodeEnum(ResponseCodeEnum.ACCESS_DENIED)
                .setData(ExceptionUtils.getRootMessage(exception))
                .build();

        response.getWriter().println(JSON.toJSONString(res));
    }
}