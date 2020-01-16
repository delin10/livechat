package nil.ed.livechat.chatroom.controller.advice;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.common.NormalResponseBuilder;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.common.ResponseCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created at 2020-01-13
 *
 * @author lidelin
 */

@Slf4j
@ControllerAdvice(basePackages = {"nil.ed.livechat.chatroom.controller"})
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Throwable.class})
    @ResponseBody
    public Response<Void> handleException(HttpServletRequest request, Throwable e) {
        log.error("Global exception: ", e);
        return new NormalResponseBuilder<Void>()
                .setCodeEnum(ResponseCodeEnum.UNCAUGHT_EXCEPTION)
                .build();
    }

}
