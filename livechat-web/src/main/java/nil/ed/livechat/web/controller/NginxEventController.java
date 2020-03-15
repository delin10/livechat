package nil.ed.livechat.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.common.ResponseCodeMessage;
import nil.ed.livechat.common.common.RoomStatusEnum;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.enums.NginxNotificationType;
import nil.ed.livechat.common.repository.RoomRepository;
import nil.ed.livechat.common.service.IRoomService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created at 2019-12-12
 *
 * 监听流媒体服务器事件并进行鉴权
 *
 * @author lidelin
 */
@Slf4j
@RestController
@RequestMapping("/live")
public class NginxEventController {

    private static final String EMPTY_NAME = "\n\n\r\r\r\n\n";

    @Resource
    private IRoomService roomService;

    @Resource
    private SessionRegistry sessionRegistry;

    @PostMapping("/notify")
    public void onPublish(@RequestHeader Map<String, String> headers,
            @RequestParam Map<String, String> params,
            @RequestParam(name = "name", defaultValue = EMPTY_NAME) Long roomId,
            @RequestParam(name = "secret_key", defaultValue = EMPTY_NAME) String secretKey,
            @RequestParam(name = "call") String call,
            @RequestParam(name = "JSESSIONID", required = false) String sessionId,
            HttpServletResponse response){
        int statusCode = HttpServletResponse.SC_OK;
        if (NginxNotificationType.PUBLISH.getCallText().equals(call)
                || NginxNotificationType.UPDATE_PUBLISH.getCallText().equals(call)) {
            statusCode = updateStatus(roomId, secretKey, RoomStatusEnum.ONLINE);
            roomService.updateLastUpdatePublishTimestamp(roomId);
        } else if (NginxNotificationType.PUBLISH_DONE.getCallText().equals(call)) {
            statusCode = updateStatus(roomId, null, RoomStatusEnum.OFFLINE);
            roomService.cleanRoom(roomId);
        } else if (NginxNotificationType.UPDATE_PLAY.getCallText().equals(call)) {
            sessionRegistry.refreshLastRequest(sessionId);
        } else if (NginxNotificationType.PLAY.getCallText().equals(call)) {
            if (sessionId == null || sessionRegistry.getSessionInformation(sessionId) == null) {
                statusCode =  HttpServletResponse.SC_FORBIDDEN;
            }
        }

        response.setStatus(statusCode);

        log.info(headers.toString());
        log.info(params.toString());
    }

    @PostMapping(value = "/connect/notify")
    public void onConnect(HttpServletResponse response) {

    }

    private int updateStatus(Long roomId, String secretKey, RoomStatusEnum status) {
        log.info("Start to invoke updateStatus with params: roomId = {}, secretKey = {}, status = {}", roomId, secretKey, status.getDesc());
        if (roomService.updateRoomStatus(roomId, secretKey, status).getCode() == ResponseCodeMessage.SUCCESS.getCode()) {
            return HttpServletResponse.SC_OK;
        } else {
            return HttpServletResponse.SC_BAD_REQUEST;
        }
    }
}
