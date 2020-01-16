package nil.ed.livechat.live.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.common.ResponseCodeEnum;
import nil.ed.livechat.chatroom.common.RoomStatusEnum;
import nil.ed.livechat.chatroom.entity.RoomEntity;
import nil.ed.livechat.chatroom.service.IRoomService;
import nil.ed.livechat.live.enums.NginxNotificationType;
import org.apache.commons.lang3.StringUtils;
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

    @PostMapping("/notify")
    public void onPublish(@RequestHeader Map<String, String> headers,
            @RequestParam Map<String, String> params,
            @RequestParam(name = "name", defaultValue = EMPTY_NAME) Long roomId,
            @RequestParam(name = "secretKey", defaultValue = EMPTY_NAME) String secretKey,
            @RequestParam(name = "call") String call,
            @RequestBody(required = false) String body,
            HttpServletResponse response) throws  Exception{
        int statusCode = HttpServletResponse.SC_OK;
        if (NginxNotificationType.PUBLISH.getCallText().equals(call)) {
            statusCode = updateStatus(roomId, secretKey, RoomStatusEnum.ONLINE);
        } else if (NginxNotificationType.DONE.getCallText().equals(call)) {
            statusCode = updateStatus(roomId, null, RoomStatusEnum.OFFLINE);
        }

        response.setStatus(statusCode);

        log.info(headers.toString());
        log.info(params.toString());
    }

    private int updateStatus(Long roomId, String secretKey, RoomStatusEnum status) {
        log.info("Start to invoke updateStatus with params: roomId = {}, secretKey = {}, status = {}", roomId, secretKey, status.getDesc());
            Response<RoomEntity> ret = roomService.getRoomById(roomId);
            if (ret.getCode() == ResponseCodeEnum.SUCCESS.getCode()) {
                RoomEntity entity = ret.getData();

                log.info("Retrieve data from db: room = {}", entity);

                if (secretKey != null && !secretKey.equals(entity.getSecretKey())){
                    return HttpServletResponse.SC_BAD_REQUEST;
                }

                log.info("Succeed to publish live stream");

                RoomEntity roomEntity = new RoomEntity();
                roomEntity.setId(entity.getId());
                roomEntity.setStatus(status.getCode());
                roomService.updateRoom(roomEntity);
            } else {
                return HttpServletResponse.SC_BAD_REQUEST;
            }
            return HttpServletResponse.SC_OK;
    }
}
