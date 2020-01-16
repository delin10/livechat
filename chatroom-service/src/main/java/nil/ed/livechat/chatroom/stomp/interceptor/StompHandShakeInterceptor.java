package nil.ed.livechat.chatroom.stomp.interceptor;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.service.IRoomService;
import nil.ed.livechat.chatroom.stomp.IRejectStrategy;
import nil.ed.livechat.chatroom.util.NameGenerator;
import nil.ed.livechat.chatroom.util.stomp.MessageContext;
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
public class StompHandShakeInterceptor implements ChannelInterceptor {
    @Resource(name = "roomService")
    private IRoomService roomService;

    private IRejectStrategy rejectStrategy;

    @Autowired
    public void setRejectStrategy(@Qualifier("simpleRejectStrategy") IRejectStrategy rejectStrategy) {
        this.rejectStrategy = rejectStrategy;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Start to invoke preSend with params: message = {}", message);
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        Assert.notNull(accessor, "StompHeaderAccessor not null");
        StompCommand command = accessor.getCommand();
        if (!StompCommand.CONNECT.equals(command)){
            return message;
        }

        String roomIdStr = roomService.resolveRoomId(accessor);
        if (!roomService.validateRoomId(message, roomIdStr)){
            String msg = String.format("不合法的房间id: %s", roomIdStr);
            log.warn(msg);
            rejectStrategy.reject(message, msg, channel);
            return null;
        }
        if (accessor.getUser() == null) {
            String name = NameGenerator.generate();
            accessor.setUser(() -> name);
        }
        MessageContext.put(message);
        return message;
    }
}
