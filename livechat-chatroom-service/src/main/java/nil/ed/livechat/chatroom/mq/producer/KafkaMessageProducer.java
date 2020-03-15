package nil.ed.livechat.chatroom.mq.producer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import nil.ed.livechat.common.service.IUserService;
import nil.ed.livechat.common.vo.UserBaseVO;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-01-03
 *
 * @author lidelin
 */

@Slf4j
@Component
public class KafkaMessageProducer {

    private KafkaProducer<String, byte[]> producer;

    @Resource
    private IUserService userService;

    @Value("${kafka.servers}")
    private String servers;

    @PostConstruct
    void init() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("buffer.memory", 32 * 1024 * 1024);
        properties.put("batch.size", 16 * 1024);
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(properties);
    }

    public boolean sendToKafka(String topic, Message<?> message) {
        try {
            CustomUserDetailsImpl user = MessageUtils.getUser(message);
            UserBaseVO userVO = userService.getUserVOById(user.getId(), false).getData();
            String parsedPayload = MessageUtils.createMessage(MessageUtils.byteArrayPayloadToString(message), userVO);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, MessageUtils.encode(message, parsedPayload));
            Future<RecordMetadata> metadataFuture = producer.send(record);
            metadataFuture.get(1, TimeUnit.SECONDS);
            return true;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }

    }
}
