package nil.ed.livechat.chatroom.stomp.interceptor.handler;

import javax.annotation.Resource;

import nil.ed.livechat.chatroom.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

/**
 * @author delin10
 * @since 2019/10/16
 **/
public class RoomCheckMessageHandler implements ExtendMessageHandler {
    @Resource(name = "roomService")
    private IRoomService roomService;

    private SimpMessagingTemplate simpMessagingTemplate;

    private DefaultSimpUserRegistry userRegistry;

    private Message<?> returnMessage;


    @Autowired
    public void setSimpMessagingTemplate(@Qualifier("simpMessagingTemplate") SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Autowired
    public void setUserRegistry(@Qualifier("userRegistry") SimpUserRegistry userRegistry) {
        this.userRegistry = (DefaultSimpUserRegistry) userRegistry;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        this.returnMessage = message;
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }
        int index = destination.lastIndexOf('.');
        if (index < 0) {
            return;
        }
        String roomIdStr = destination.substring(index + 1);
        Long roomId = null;
        try {
            roomId = Long.parseLong(roomIdStr);
        } catch (NumberFormatException e) {
            this.returnMessage = null;
            return;
        }

        if (!roomService.checkExist(roomId)) {
//            accessor.setDestination(destination + "-user" + accessor.getSessionId());
//            simpMessagingTemplate.send(destination + "-user" + accessor.getSessionId(), message);
            SimpUser user = userRegistry.getUser(accessor.getUser().getName());
            SimpMessageHeaderAccessor simpMessageHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.UNSUBSCRIBE);
            simpMessageHeaderAccessor.setHeader("stompCommand", StompCommand.UNSUBSCRIBE);
            simpMessageHeaderAccessor.setSessionId(accessor.getSessionId());
            simpMessageHeaderAccessor.setDestination(accessor.getDestination());
            Message<?> msg = MessageBuilder.createMessage(new byte[0], simpMessageHeaderAccessor.getMessageHeaders());
            this.returnMessage = msg;
        }
    }

    @Override
    public boolean support(Message<?> message) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        Assert.notNull(accessor, "StompHeaderAccessor cannot be null");

        StompCommand command = accessor.getCommand();
        return StompCommand.SEND.equals(command)
                || StompCommand.SUBSCRIBE.equals(command);
    }

    @Override
    public Message<?> getReturnMessage(Message<?> message) {
        return this.returnMessage;
    }

    @Override
    public boolean supportReturnMessage(Message<?> message) {
        return true;
    }
}
