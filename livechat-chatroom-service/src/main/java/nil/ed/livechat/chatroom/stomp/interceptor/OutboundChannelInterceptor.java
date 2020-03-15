package nil.ed.livechat.chatroom.stomp.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.service.IRoomService;
import nil.ed.livechat.chatroom.stomp.ConnectedCallback;
import nil.ed.livechat.chatroom.util.sensitive.SensitiveFilter;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/24
 **/
@Slf4j
public class OutboundChannelInterceptor implements ChannelInterceptor {

    private SensitiveFilter sensitiveFilter;

    private IRoomService roomService;

    private ConnectedCallback connectedCallback;

    public void setConnectedCallback(ConnectedCallback connectedCallback) {
        this.connectedCallback = connectedCallback;
    }

    @Autowired
    public void setRoomService(@Qualifier("roomService") IRoomService roomService) {
        this.roomService = roomService;
    }

    @Autowired
    public void setSensitiveFilter(SensitiveFilter sensitiveFilter) {
        this.sensitiveFilter = sensitiveFilter;
    }

    @Override

    public Message<?> preSend( Message<?> message, MessageChannel channel) {
        message = MessageUtils.setStompHeaderAccessor(message);
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info(String.valueOf(accessor.getMessageHeaders()));
        Assert.notNull(accessor, "Accessor cannot be null");

        if (StompCommand.MESSAGE.equals(accessor.getCommand())){
            String payload = MessageUtils.byteArrayPayloadToString(message);
            String markPayload = sensitiveFilter.filter(payload).getMaskText();
            return MessageBuilder.createMessage(markPayload.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
        }

        return message;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if (!sent){
            return;
        }

        log.info("Succeed to send message: {}", message);
        StompHeaderAccessor accessor = MessageUtils.getStompHeaderAccessor(message);
        Assert.notNull(accessor, "StompHeaderAccessor not null");

        //在preSend时还没有建立连接, 所以那个时候无法发送到消息代理中导致消息丢失

        StompCommand command = accessor.getCommand();
        SimpMessageType type = accessor.getMessageType();
        log.info("Handshake response: {}", command);

        try {
            if (StompCommand.CONNECTED.equals(command) || SimpMessageType.CONNECT_ACK.equals(type)) {
                MessageHeaders connectHeaders = Optional.ofNullable((Message<?>) accessor
                        .getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER))
                        .map(Message::getHeaders)
                        .orElseThrow(() -> new InvalidHeaderException(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER, null));
                Map<String, List<String>> nativeHeaders = (Map<String, List<String>>)connectHeaders.get(NativeMessageHeaderAccessor.NATIVE_HEADERS);
                if (nativeHeaders == null) {
                    throw new InvalidHeaderException("nativeHeaders", null);
                }
                log.info("connect headers: {}", nativeHeaders);
                String roomIdStr = Optional.ofNullable(nativeHeaders.get("room-id"))
                        .map(ls -> ls.get(0))
                        .orElseThrow(() -> new InvalidHeaderException("room-id", null));
                connectedCallback.onConnected(roomIdStr, message);
            }
        } catch (Exception e) {
            log.error("process error: ", e);
        }
    }
}
