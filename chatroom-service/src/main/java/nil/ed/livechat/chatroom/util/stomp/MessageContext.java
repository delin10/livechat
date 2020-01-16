package nil.ed.livechat.chatroom.util.stomp;

import org.springframework.messaging.Message;

/**
 * Created at 2020-01-14
 *
 * @author lidelin
 */

public class MessageContext {

    private static ThreadLocal<Message<?>> messageContext = new ThreadLocal<>();

    public static void put(Message<?> message) {
        messageContext.set(message);
    }

    public static Message<?> getAndRemove() {
        Message<?> result = messageContext.get();
        messageContext.remove();
        return result;
    }

}
