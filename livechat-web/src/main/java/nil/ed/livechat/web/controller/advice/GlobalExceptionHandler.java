package nil.ed.livechat.web.controller.advice;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeMessage;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created at 2020-01-13
 *
 * @author lidelin
 */

@Slf4j
@ControllerAdvice(basePackages = {"nil.ed.livechat.web.controller"})
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseBody
    public Response<Void> handleMethodArgumentNotValidException(Throwable e) {
        log.error("Validation exception: ", e);
            return new NormalResponseBuilder<Void>()
                    .setCodeMessage(ResponseCodeMessage.PARAM_VALIDATE_FAILED)
                    .setMessage(e.getMessage())
                    .build();
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    @ResponseBody
    public Response<Void> handleDuplicateKeyException(Throwable e) {
        return new NormalResponseBuilder<Void>()
                .setCodeMessage(ResponseCodeMessage.PARAM_VALIDATE_FAILED)
                .setMessage(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Response<Void> handleException(Exception e) {
        return new NormalResponseBuilder<Void>()
                .setCodeMessage(ResponseCodeMessage.FAILED)
                .setMessage(e.getMessage())
                .build();
    }
}
