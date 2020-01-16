package nil.ed.livechat.chatroom.stomp;

import org.springframework.messaging.Message;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public interface ConnectedCallback {
    /**
     * 连接成功后的回调
     *
     * @param message
     */
    void onConnected(String roomId, Message<?> message);
}
