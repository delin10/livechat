package nil.ed.livechat.chatroom.component;

import javax.annotation.Resource;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.stomp.subscription.CustomSubscriptionRegistry;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Created at 2020-01-12
 *
 * @author lidelin
 */
@Slf4j
public class EchoMessageListener implements MessageListener {

    private SimpMessagingTemplate simpMessagingTemplate;

    public EchoMessageListener(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            log.info("Start to consume redis message with params: message = {}", message);
            List<org.springframework.messaging.Message<byte[]>> messageList = MessageUtils.decode(message.getBody());
            messageList.forEach(simpMessagingTemplate::send);
            log.info("Succeed to consume redis message");
        } catch (Exception e) {
            log.error("Failed to consume redis message!", e);
        }
    }
}
