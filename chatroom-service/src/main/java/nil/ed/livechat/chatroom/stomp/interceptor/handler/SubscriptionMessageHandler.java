package nil.ed.livechat.chatroom.stomp.interceptor.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/16
 **/
public class SubscriptionMessageHandler implements ExtendMessageHandler {
    private Message<?> returnMessage;
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        returnMessage = message;
    }

    @Override
    public boolean support(Message<?> message) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        Assert.notNull(accessor, "StompHeaderAccessor cannot be null");
        return false;
    }

    @Override
    public Message<?> getReturnMessage(Message<?> message) {
        return null;
    }

    @Override
    public boolean supportReturnMessage(Message<?> message) {
        return false;
    }
}
