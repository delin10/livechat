package nil.ed.livechat.common.util.redis;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Created at 2020-01-12
 *
 * @author lidelin
 */

@Slf4j
public class RedisSubscriber {

    private Thread subscribeThread;

    private RedisConnectionFactory redisConnectionFactory;

    private MessageListener listener;

    private Set<byte[]> channels = new HashSet<>();

    private RedisConnection redisConnection;

    public RedisSubscriber(RedisConnectionFactory redisConnectionFactory, Set<byte[]> channels, MessageListener listener) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.listener = listener;
        this.channels.addAll(channels);
    }

    public MessageListener getListener() {
        return listener;
    }

    public Set<byte[]> getChannels() {
        return channels;
    }

    public void destroy() {
        Optional.ofNullable(this.redisConnection)
                .map(RedisConnection::getSubscription)
                .ifPresent(sub -> sub.unsubscribe(getSerializedChannels()));
        Optional.ofNullable(this.redisConnection).ifPresent(RedisConnection::close);
    }

    public void subscribe() {
        this.subscribeThread = new Thread(() -> {
            byte[][] serializedChannels = getSerializedChannels();
            //noinspection AlibabaAvoidManuallyCreateThread
            try {
                this.redisConnection = redisConnectionFactory.getConnection();
                redisConnection.subscribe(getListener(), serializedChannels);
            } catch (Exception e) {
                log.error("error when subscribe {}!", channels, e);
            }
        }, "subscribe-thread-" + this.hashCode());
        this.subscribeThread.start();
    }

    private byte[][] getSerializedChannels() {
        return getChannels().toArray(new byte[0][]);
    }

    public static class SubscribeConfig {

        private Set<byte[]> channel;

        private MessageListener listener;

        public SubscribeConfig(Set<byte[]> channel, MessageListener listener) {
            this.channel = channel;
            this.listener = listener;
        }

        public RedisSubscriber getRedisSubscriber(RedisConnectionFactory redisConnectionFactory) {
            return new RedisSubscriber(redisConnectionFactory, channel, listener);
        }

    }

}
