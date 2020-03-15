package nil.ed.livechat.chatroom.stomp;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

/**
 * 拒绝策略
 *
 * @author delin10
 * @since 2019/10/18
 **/
public interface IRejectStrategy {
    /**
     *拒绝
     *
     * @param message 消息
     * @param rejectDescription 拒绝信息描述
     */
    void reject(Message<?> message, String rejectDescription, MessageChannel inboundChannel);
}
