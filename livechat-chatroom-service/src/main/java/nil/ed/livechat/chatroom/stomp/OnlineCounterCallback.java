package nil.ed.livechat.chatroom.stomp;

import javax.annotation.Resource;

import nil.ed.livechat.common.service.IRoomService;
import org.springframework.messaging.Message;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public class OnlineCounterCallback implements ConnectedCallback {

    @Resource
    private IRoomService roomService;

    @Override
    public void onConnected(String roomId, Message<?> message) {
        roomService.updateRoomChatOnlineCount(Long.parseLong(roomId), 1);
    }
}
