package nil.ed.livechat.chatroom.stomp;

import javax.annotation.Resource;

import nil.ed.livechat.chatroom.stomp.subscription.CustomSubscriptionRegistry;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SubscriptionRegistry;
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
@Component("echoRejectStrategy")
public class EchoRejectStrategy implements IRejectStrategy {

    @Resource
    private CustomSubscriptionRegistry subscriptionRegistry;

    @Resource
    private RoomSessionRegistry roomSessionRegistry;

    private MessageChannel outboundChannel;

    @Autowired
    public void setOutboundChannel(@Qualifier("clientOutboundChannel") MessageChannel outboundChannel) {
        this.outboundChannel = outboundChannel;
    }

    @Override
    public void reject(Message<?> message, String rejectDescription, MessageChannel inboundChannel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "accessor cannot be null");

        Long roomId = roomSessionRegistry.getRoomId(accessor.getSessionId());
        String dest = "/topic/echo." + roomId;
        subscriptionRegistry.findByDestination(dest, accessor.getSessionId()).forEach(subId -> {
            StompHeaderAccessor frame = StompHeaderAccessor.create(StompCommand.MESSAGE);
            frame.setDestination(dest);
            frame.setSubscriptionId(subId);
            frame.setSessionId(accessor.getSessionId());
            String payload = MessageUtils.createMessage(rejectDescription, null);
            outboundChannel.send(MessageBuilder.createMessage(payload, frame.getMessageHeaders()));
        });
    }
}
