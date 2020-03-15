package nil.ed.livechat.chatroom.stomp.interceptor;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.stomp.RoomSessionRegistry;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import nil.ed.livechat.common.service.IRoomService;
import nil.ed.livechat.chatroom.stomp.IRejectStrategy;
import nil.ed.livechat.common.util.Checker;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/17
 **/
@Slf4j
public class StompMessageInterceptor implements ChannelInterceptor {

    @Resource(name = "roomService")
    private IRoomService roomService;

    private IRejectStrategy rejectStrategy;

    @Resource(name = "echoRejectStrategy")
    private IRejectStrategy echoRejectStrategy;

    @Resource
    private RoomSessionRegistry roomSessionRegistry;

    private Set<String> validPrefixes = new HashSet<>(4);

    private Set<String> userPrefixes = new HashSet<>(4);

    {
        validPrefixes.add("/topic/echo");
        validPrefixes.add("/topic/group");
        validPrefixes.add("/topic/oneToOne");

        userPrefixes.add("/user");
    }

    @Autowired
    public void setRejectStrategy(@Qualifier("simpleRejectStrategy") IRejectStrategy rejectStrategy) {
        this.rejectStrategy = rejectStrategy;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Message: {}", message);
        StompHeaderAccessor accessor = MessageUtils.getStompHeaderAccessor(message);

        CustomUserDetailsImpl user = MessageUtils.getUser(message);
        StompCommand command = accessor.getCommand();

        Long roomId = roomSessionRegistry.getRoomId(accessor.getSessionId());
        if (StompCommand.SEND.equals(command)
                || StompCommand.SUBSCRIBE.equals(command)
                || StompCommand.UNSUBSCRIBE.equals(command)){
            if (StompCommand.SEND.equals(command)){

                if (!check(accessor, message, channel)) {
                    return null;
                }

                boolean ok = roomService.incrementChatRecord(roomId, user.getId(), 1L);
                if (!ok) {
                    String msg = "发言过于频繁";
                    echoRejectStrategy.reject(message, msg, channel);
                    return null;
                }
                accessor.setDestination("/kafka" + accessor.getDestination());
                return message;
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            roomService.updateRoomChatOnlineCount(roomId, -1);
        }
        return message;
    }

    private boolean hasUserPrefix(String pattern){
        return userPrefixes.stream().
                anyMatch(pattern::startsWith);
    }

    private boolean check(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        String dest = accessor.getDestination();
        Assert.notNull(dest, "Destination not null");
        int dotLastIndex = dest.lastIndexOf('.');
        String prefix = dest.substring(0, dotLastIndex > 0 ? dotLastIndex : dest.length());

        if (!hasUserPrefix(dest) && !validPrefixes.contains(prefix)){
            String msg = String.format("不合法的前缀: %s", prefix);
            log.warn(msg);
            rejectStrategy.reject(message, msg, channel);
            return false;
        }
        return true;
    }
}
