package nil.ed.livechat.chatroom.stomp.interceptor.handler;

import javax.annotation.Resource;

import nil.ed.livechat.chatroom.service.IRoomService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/16
 **/
public class HandshakeMessageHandler implements ExtendMessageHandler {
    @Resource(name = "roomService")
    private IRoomService roomService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

    }

    @Override
    public boolean support(Message<?> message) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        Assert.notNull(accessor, "StompHeaderAccessor cannot be null");

        StompCommand command = accessor.getCommand();
        return StompCommand.CONNECT.equals(command);
    }


}
