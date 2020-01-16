package nil.ed.livechat.chatroom.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/18
 **/
@Component("simpleRejectStrategy")
public class SimpleRejectStrategy implements IRejectStrategy {
    private MessageChannel outboundChannel;

    @Autowired
    public void setOutboundChannel(@Qualifier("clientOutboundChannel") MessageChannel outboundChannel) {
        this.outboundChannel = outboundChannel;
    }

    @Override
    public void reject(Message<?> message, String rejectDescription, MessageChannel inboundChannel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "accessor cannot be null");
        StompHeaderAccessor errorFrameHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorFrameHeaderAccessor.setMessage(rejectDescription);
        errorFrameHeaderAccessor.setSessionId(accessor.getSessionId());
        outboundChannel.send(MessageBuilder.createMessage(new byte[0],
                errorFrameHeaderAccessor.getMessageHeaders()));
    }
}
