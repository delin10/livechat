package nil.ed.livechat.chatroom;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public class RedisTest extends AbstractServiceTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test(){
        String test = redisTemplate.opsForValue().get("test");
        System.out.println(test);
    }

    @Test
    public void zsetTest() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String key ="key";

        Random random = new Random();
        IntStream.range(0, 1000).forEach(i ->{
            Integer v = random.nextInt();
            zSetOperations.add(key, key+""+ v, v);
            zSetOperations.incrementScore(key, key + "" + v, 1);
        });

        System.out.println(zSetOperations.rangeWithScores(key, 0, 50));
    }
}
