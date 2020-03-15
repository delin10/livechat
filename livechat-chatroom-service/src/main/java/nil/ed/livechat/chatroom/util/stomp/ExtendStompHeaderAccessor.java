package nil.ed.livechat.chatroom.util.stomp;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * Created at 2020-01-13
 *
 * @author lidelin
 */

public class ExtendStompHeaderAccessor {

    public static Long getUid(Message<?> message) {
        MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, null);

        Assert.notNull(accessor, "accessor must not be null");

        try {
            return Optional.ofNullable(accessor.getHeader("uid"))
                    .map(Object::toString)
                    .map(Long::parseLong)
                    .orElse(null);
        }catch (NumberFormatException e) {
            return null;
        }
    }

    public static void setUid(Integer uid, MessageHeaders headers) {
        headers.put("uid", uid);
    }

}
