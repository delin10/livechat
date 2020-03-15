package nil.ed.livechat.chatroom.stomp.interceptor.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

/**
 * @author delin10
 * @since 2019/10/16
 **/
public interface ExtendMessageHandler extends MessageHandler {
    /**
     * 判断handler是否支持该消息
     *
     * @param message
     * @return
     */
    default boolean support(Message<?> message){
        return true;
    }

    /**
     * 获取处理后可能返回的自定义消息
     *
     * @param message 消息
     * @return
     */
    default Message<?> getReturnMessage(Message<?> message){
        return null;
    }

    /**
     * 是否支持返回消息
     * @param message 消息
     * @return
     */
    default boolean supportReturnMessage(Message<?> message){
        return false;
    }
}
