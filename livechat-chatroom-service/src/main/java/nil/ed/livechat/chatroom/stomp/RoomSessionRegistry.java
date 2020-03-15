package nil.ed.livechat.chatroom.stomp;

import javax.annotation.Resource;
import java.util.Map;

import nil.ed.livechat.common.common.RedisPrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-03-13
 *
 * @author lidelin
 */
@Component
public class RoomSessionRegistry {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    public void registerRoomId(String sessionId, Long roomId) {
        redisTemplate.opsForHash().put(RedisPrefix.STOMP_SESSION_ROOM_MAP.getRawKey(), sessionId, roomId);
    }

    public void remove(String sessionId) {
        redisTemplate.opsForHash().delete(RedisPrefix.STOMP_SESSION_ROOM_MAP.getRawKey(), sessionId);
    }

    public Long getRoomId(String sessionId) {
        return (Long) redisTemplate.opsForHash().get(RedisPrefix.STOMP_SESSION_ROOM_MAP.getRawKey(), sessionId);
    }

}
