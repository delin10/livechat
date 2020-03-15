package nil.ed.livechat.common.util.redis;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-01-12
 *
 * @author lidelin
 */

@Component
public class RedisPublisher {

    private RedisConnectionFactory  redisConnectionFactory;

    public RedisPublisher(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public void publish(byte[] key, byte[] data) {
        redisConnectionFactory.getConnection().publish(key, data);
    }

    public void publishJson(String channel, Object data) {
        publish(channel.getBytes(StandardCharsets.UTF_8), JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8));
    }

}
