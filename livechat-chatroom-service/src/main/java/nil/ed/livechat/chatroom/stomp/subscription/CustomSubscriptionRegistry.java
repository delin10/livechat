package nil.ed.livechat.chatroom.stomp.subscription;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MultiValueMap;

/**
 * Created at 2020-03-13
 *
 * @author lidelin
 */

public class CustomSubscriptionRegistry extends DefaultSubscriptionRegistry {

    private static final Message<?> TEST_MSG = MessageBuilder.createMessage(new byte[0],
            StompHeaderAccessor.create(StompCommand.MESSAGE).getMessageHeaders());


    public List<String> findByDestination(String dest, String sessionId) {
        MultiValueMap<String, String> result = findSubscriptionsInternal(dest, TEST_MSG);
        return result.get(sessionId);
    }

}
