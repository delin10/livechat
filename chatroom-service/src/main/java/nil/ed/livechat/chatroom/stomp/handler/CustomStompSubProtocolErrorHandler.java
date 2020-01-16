package nil.ed.livechat.chatroom.stomp.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

/**
 * @author delin10
 * @since 2019/10/17
 **/
@Slf4j
public class CustomStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler {
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        log.error("系统错误:", cause);
        errorHeaderAccessor.setMessage("系统错误！");
        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
    }
}
