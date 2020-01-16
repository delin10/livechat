package nil.ed.livechat.chatroom.component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nil.ed.livechat.chatroom.common.RedisPrefix;
import nil.ed.livechat.chatroom.util.redis.RedisPublisher;
import nil.ed.livechat.chatroom.util.redis.RedisSubscriber;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-01-12
 *
 * @author lidelin
 */

@Component
public class RedisPubSubComponent implements DisposableBean {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    private RedisPublisher redisPublisher;

    private StompEncoder stompEncoder = new StompEncoder();

    private RedisSubscriber redisSubscriber;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    void init() {
        this.redisPublisher = new RedisPublisher(redisConnectionFactory);
        Set<byte[]> channels = new HashSet<>(2);
        Collections.addAll(channels, RedisPrefix.OUT_BOUND_ECHO_CALLBACK_PUB_SUB.getRawKeyBytesUtf8());
        this.redisSubscriber = new RedisSubscriber.SubscribeConfig(channels, new EchoMessageListener(simpMessagingTemplate))
                .getRedisSubscriber(redisConnectionFactory);
        this.redisSubscriber.subscribe();
    }

    public void publishMessage(Message<byte[]> message) {
        redisPublisher.publish(RedisPrefix.OUT_BOUND_ECHO_CALLBACK_PUB_SUB.getRawKeyBytesUtf8(),
                stompEncoder.encode(message));
    }

    @Override
    public void destroy() {
        redisSubscriber.destroy();
    }

}
