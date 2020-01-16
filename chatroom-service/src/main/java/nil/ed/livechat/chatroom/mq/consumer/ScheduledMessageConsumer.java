package nil.ed.livechat.chatroom.mq.consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.common.KafkaConstants;
import nil.ed.livechat.chatroom.common.RedisPrefix;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-01-05
 *
 * @author lidelin
 */

@Slf4j
@Component
public class ScheduledMessageConsumer {

    @Value("${kafka.servers}")
    private String servers;

    private SimpMessagingTemplate simpMessagingTemplate;

    private KafkaConsumer<String, byte[]> consumer;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @PostConstruct
    void init() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.setProperty("group.id",
                String.format(KafkaConstants.CONSUMER_GROUP,
                        redisTemplate.opsForValue().increment(RedisPrefix.GROUP_ID_KEY.getRawKey())));
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(KafkaConstants.TOPIC));
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void schedule() {
        try {
            log.info("Start to consume message...");
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));

            for (ConsumerRecord<String, byte[]> record : records) {
                List<Message<byte[]>> messageList = MessageUtils.encode(record.value());
                log.info("Consuming message = {}", messageList);
                messageList.forEach(simpMessagingTemplate::send);
            }
            log.info("Succeed to consume message, count = {}", records.count());
        } catch (Exception e) {
            log.error("Failed to consume message!", e);
        }
    }
}
