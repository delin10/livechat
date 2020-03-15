package nil.ed.livechat.chatroom.util.stomp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.common.MessageVO;
import nil.ed.livechat.common.vo.UserBaseVO;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/24
 **/
@Slf4j
public class MessageUtils {

    private MessageUtils() {
    }

    /**
     * 与Spring StompSubProtocolHandler相一致
     */
    private static final String[] SUPPORTED_VERSIONS = {"1.2", "1.1", "1.0"};

    /**
     *  将byte[]类型的payload转换成字符串
     *
     * @param message 不进行空值检查
     * @return 采用UTF-8编码的字符串payload
     */
    public static String byteArrayPayloadToString(Message<?> message){
        /*
        never null
         */
        Object payload = message.getPayload();
        if (payload instanceof byte[]) {
            return new String((byte[])payload, StandardCharsets.UTF_8);
        } else if (payload instanceof String){
            return payload.toString();
        }
        return null;
    }

    public static Message<?> setStompHeaderAccessor(Message<?> message){
        StompHeaderAccessor stompHeaderAccessor = convertSimpHeadersAccessorToStompHeadersAccessor(message);
        MessageHeaders headers = stompHeaderAccessor.getMessageHeaders();
        return MessageBuilder.createMessage(message.getPayload(), headers);
    }

    public static StompHeaderAccessor getStompHeaderAccessor(Message<?> message){
        return convertSimpHeadersAccessorToStompHeadersAccessor(message);
    }

    private static StompHeaderAccessor convertSimpHeadersAccessorToStompHeadersAccessor(Message<?> message){
        MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
        Assert.notNull(accessor, "Accessor cannot be null!");

        if (accessor instanceof StompHeaderAccessor){
            return (StompHeaderAccessor) accessor;
        }
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(message);
        SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(message.getHeaders());

        if (SimpMessageType.CONNECT_ACK.equals(messageType)) {
            convertConnectAcktoStompConnected(stompAccessor);
        }
        else if (SimpMessageType.DISCONNECT_ACK.equals(messageType)) {
            String receipt = getDisconnectReceipt(stompAccessor);
            if (receipt != null) {
                stompAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                stompAccessor.setReceiptId(receipt);
            }
            else {
                stompAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
                stompAccessor.setMessage("Session closed.");
            }
        }
        else if (SimpMessageType.HEARTBEAT.equals(messageType)) {
            stompAccessor = StompHeaderAccessor.createForHeartbeat();
        }
        else if (stompAccessor.getCommand() == null || StompCommand.SEND.equals(stompAccessor.getCommand())) {
            stompAccessor.updateStompCommandAsServerMessage();
        }
        String origDestination = stompAccessor.getFirstNativeHeader(SimpMessageHeaderAccessor.ORIGINAL_DESTINATION);
        if (origDestination != null) {
            stompAccessor.removeNativeHeader(SimpMessageHeaderAccessor.ORIGINAL_DESTINATION);
            stompAccessor.setDestination(origDestination);
        }

        return stompAccessor;
    }

    public static StompHeaderAccessor toMutableAccessor(StompHeaderAccessor headerAccessor, Message<?> message) {
        return StompHeaderAccessor.wrap(message);
    }

    /**
     * The simple broker produces {@code SimpMessageType.CONNECT_ACK} that's not STOMP
     * specific and needs to be turned into a STOMP CONNECTED frame.
     */
    private static void convertConnectAcktoStompConnected(StompHeaderAccessor stompAccessor) {
        String name = StompHeaderAccessor.CONNECT_MESSAGE_HEADER;
        Message<?> message = (Message<?>) stompAccessor.getHeader(name);
        if (message == null) {
            throw new IllegalStateException("Original STOMP CONNECT not found in " + stompAccessor);
        }

        StompHeaderAccessor connectHeaders = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        Assert.notNull(connectHeaders, "MessageHeaders cannot be null!");

        stompAccessor.setHeader("stompCommand", StompCommand.CONNECTED);
        Set<String> acceptVersions = connectHeaders.getAcceptVersion();
        stompAccessor.setVersion(
                    Arrays.stream(SUPPORTED_VERSIONS)
                            .filter(acceptVersions::contains)
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unsupported STOMP version '" + acceptVersions + "'")));

        long[] heartbeat = (long[]) stompAccessor.getHeader(SimpMessageHeaderAccessor.HEART_BEAT_HEADER);
        if (heartbeat != null) {
            stompAccessor.setHeartbeat(heartbeat[0], heartbeat[1]);
        }
        else {
            stompAccessor.setHeartbeat(0, 0);
        }
    }

    @Nullable
    private static String getDisconnectReceipt(SimpMessageHeaderAccessor simpHeaders) {
        String name = StompHeaderAccessor.DISCONNECT_MESSAGE_HEADER;
        Message<?> message = (Message<?>) simpHeaders.getHeader(name);
        if (message != null) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (accessor != null) {
                return accessor.getReceipt();
            }
        }
        return null;
    }

    private static StompEncoder stompEncoder = new StompEncoder();

    private static StompDecoder stompDecoder = new StompDecoder();

    public static List<Message<byte[]>> decode(byte[] bytes) {
        return stompDecoder.decode(ByteBuffer.wrap(bytes));
    }

    public static byte[] encode(Message<?> message, String useOtherPayload) {
        StompHeaderAccessor accessor = getStompHeaderAccessor(message);
        byte[] payload;
        if (useOtherPayload == null) {
            if (message.getPayload() instanceof String) {
                payload = ((String) message.getPayload()).getBytes(StandardCharsets.UTF_8);
            } else if (message.getPayload() instanceof byte[]) {
                payload = (byte[]) message.getPayload();
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            payload = useOtherPayload.getBytes();
        }

        return stompEncoder.encode(accessor.getMessageHeaders(), payload);
    }

    public static CustomUserDetailsImpl getUser(Message<?> message) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)getStompHeaderAccessor(message).getUser();
        log.info("Get token = {}", token);
        Assert.notNull(token, "Token must not be null!");
        CustomUserDetailsImpl user = (CustomUserDetailsImpl) token.getPrincipal();
        log.info("Get user = {}", user);
        Assert.notNull(user, "User must not be null!");
        return user;
    }

    public static String createMessage(String message, UserBaseVO user) {
        MessageVO messageVO = new MessageVO();
        messageVO.setContent(message);
        messageVO.setUser(user);
        return JSON.toJSONString(messageVO);
    }

}
