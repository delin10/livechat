package nil.ed.livechat.chatroom.stomp.interceptor;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.mq.producer.KafkaMessageProducer;
import nil.ed.livechat.chatroom.service.IRoomService;
import nil.ed.livechat.chatroom.stomp.IRejectStrategy;
import nil.ed.livechat.chatroom.util.Checker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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

    private KafkaMessageProducer producer;

    private IRejectStrategy rejectStrategy;

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

    @Autowired
    public void setProducer(KafkaMessageProducer kafkaMessageProducer) {
        this.producer = kafkaMessageProducer;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Message: {}", message);
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "StompHeaderAccessor not null");

        StompCommand command = accessor.getCommand();
        if (StompCommand.SEND.equals(command)
                || StompCommand.SUBSCRIBE.equals(command)
                || StompCommand.UNSUBSCRIBE.equals(command)){
            String dest = accessor.getDestination();
            Assert.notNull(dest, "Destination not null");
            int dotLastIndex = dest.lastIndexOf('.');
            String prefix = dest.substring(0, dotLastIndex > 0 ? dotLastIndex : dest.length());



            if (!hasUserPrefix(dest) && !validPrefixes.contains(prefix)){
                String msg = String.format("不合法的前缀: %s", prefix);
                log.warn(msg);
                rejectStrategy.reject(message, msg, channel);
                return null;
            }

            String roomId = null;
            if (dotLastIndex > 0){
                roomId = dest.substring( dotLastIndex + 1);
            }

            if (!roomService.validateRoomId(message, roomId)){
                String msg = String.format("不合法的房间id: %s", roomId);
                log.warn(msg);
                rejectStrategy.reject(message, msg, channel);
                return null;
            }

            if (StompCommand.SEND.equals(command)){
                roomService.incrementChatRecord(Checker.checkPureLongNumber(roomId).longValue(), (long) accessor.getUser().getName().hashCode(), 1L);
                accessor.setDestination("/kafka" + accessor.getDestination());
                return message;
            }
        }
        return message;
    }

    private boolean hasUserPrefix(String pattern){
        return userPrefixes.stream().
                anyMatch(pattern::startsWith);
    }
}
