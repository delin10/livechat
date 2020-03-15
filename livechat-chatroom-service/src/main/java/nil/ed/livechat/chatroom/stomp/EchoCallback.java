package nil.ed.livechat.chatroom.stomp;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.component.RedisPubSubComponent;
import nil.ed.livechat.chatroom.util.stomp.MessageUtils;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.service.IRoomService;
import nil.ed.livechat.common.service.IUserService;
import nil.ed.livechat.common.vo.UserBaseVO;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * @author delin10
 * @since 2019/10/21
 **/
@Slf4j
public class EchoCallback implements ConnectedCallback {

    private static final String ECHO_PATTERN = "%s加入房间，当前房间人数为:%s人";

    private ValueOperations<String, String> valueOperations;

    @Resource
    private RedisPubSubComponent redisPubSubComponent;

    @Resource
    private IUserService userService;

    @Resource
    private IRoomService roomService;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.valueOperations = stringRedisTemplate.opsForValue();
    }

    @Override
    public void onConnected(String roomId, Message<?> message) {
        StompHeaderAccessor accessor = MessageUtils.getStompHeaderAccessor(message);
        Assert.notNull(accessor, "accessor not null");
        StompHeaderAccessor header = StompHeaderAccessor.create(StompCommand.MESSAGE);
        header.setSessionId(accessor.getSessionId());
        header.setDestination("/topic/echo." + roomId);
        log.info("accessor.getUser={}", accessor.getUser());
        CustomUserDetailsImpl user = MessageUtils.getUser(message);
        String name = Optional.ofNullable(userService.getUserVOById(user.getId(), false))
                .map(Response::getData)
                .map(UserBaseVO::getNickname).orElse("unknown");
        String payload = MessageUtils.createMessage(String.format(ECHO_PATTERN, name, roomService.getRoomChatOnlineCount(Long.parseLong(roomId))), null);
        Message<byte[]> msg = MessageBuilder.createMessage(payload.getBytes(StandardCharsets.UTF_8), header.getMessageHeaders());
        log.info("Publish redis message: {}", msg);
        redisPubSubComponent.publishMessage(msg);
    }
}
