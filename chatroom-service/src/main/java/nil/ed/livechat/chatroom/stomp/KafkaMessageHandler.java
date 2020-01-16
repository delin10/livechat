package nil.ed.livechat.chatroom.stomp;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.common.KafkaConstants;
import nil.ed.livechat.chatroom.mq.producer.KafkaMessageProducer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * Created at 2020-01-13
 *
 * @author lidelin
 */
@Slf4j
public class KafkaMessageHandler implements MessageHandler, DisposableBean {

    @Resource
    private KafkaMessageProducer kafkaMessageProducer;

    private String prefix = "/kafka";

    private SubscribableChannel clientInboundChannel;

    public KafkaMessageHandler(SubscribableChannel clientInboundChannel) {
        this.clientInboundChannel = clientInboundChannel;
    }

    @PostConstruct
    void init() {
        clientInboundChannel.subscribe(this);
    }

    @Override
    public void destroy() throws Exception {
        clientInboundChannel.unsubscribe(this);
    }

    @Override
    public void handleMessage(Message<?> message) {
        MessageHeaders headers = message.getHeaders();
        SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(headers);
        String destination = SimpMessageHeaderAccessor.getDestination(headers);
        if (destination == null || !destination.startsWith(prefix)) {
            return;
        }

        if (SimpMessageType.MESSAGE.equals(messageType)) {
            log.info("Before converted, message = {}", message);
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
            Assert.notNull(accessor, "accessor cannot be null");
            String actualDestination = destination.substring(prefix.length());
            Object payload = message.getPayload();
            accessor.setDestination(actualDestination);
            byte[] payloadBytes = payload instanceof byte[] ? (byte[]) payload : payload.toString().getBytes(StandardCharsets.UTF_8);
            Message<byte[]> convertedMessage = MessageBuilder.createMessage(payloadBytes, accessor.getMessageHeaders());
            log.info("After converted, message = {}", convertedMessage);
            kafkaMessageProducer.sendToKafka(KafkaConstants.TOPIC, convertedMessage);
        }
    }

}
