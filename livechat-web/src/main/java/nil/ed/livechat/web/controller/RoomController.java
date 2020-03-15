package nil.ed.livechat.web.controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import nil.ed.livechat.common.common.PageResult;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.service.IRoomService;
import nil.ed.livechat.common.validator.annotation.InsertValidatedGroup;
import nil.ed.livechat.common.validator.annotation.UpdateValidatedGroup;
import nil.ed.livechat.common.vo.IndexRoomListVO;
import nil.ed.livechat.common.vo.RoomFormVO;
import nil.ed.livechat.common.vo.SubscribedRoomVO;
import nil.ed.livechat.login.security.AuthenticationConstants;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@RestController
@RequestMapping("/livechat/room")
public class RoomController {
    @Resource(name = "roomService")
    private IRoomService roomService;

    @PostMapping("/create")
    public Response<Void> addRoom(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestBody(required = false) @Validated(InsertValidatedGroup.class) RoomFormVO room){
        RoomEntity roomEntity = room.toRoomEntity();
        roomEntity.setUid(user.getId());
        return roomService.addRoom(roomEntity, room.getTags());
    }

    @PostMapping("/edit")
    public Response<Void> updateRoom(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestBody(required = false) @Validated(UpdateValidatedGroup.class) RoomFormVO room){
        RoomEntity roomEntity = room.toRoomEntity();
        roomEntity.setUid(user.getId());
        return roomService.updateRoom(roomEntity, room.getTags());
    }

    @PostMapping("/delete/{roomId}")
    public Response<Void> deleteRoom(@PathVariable("roomId") Long roomId){
        return roomService.deleteRoom(roomId, 1L);
    }

    @GetMapping("/list")
    public Response<PageResult<RoomEntity>> listRooms(){
        return roomService.listRooms();
    }

    @GetMapping("/{roomId}/user/list/rank")
    public Response<List<Object>> listRooms(@PathVariable("roomId") Long roomId){
        return roomService.listRankUsersTop50(roomId);
    }

    @GetMapping("/info/get")
    public Response<SubscribedRoomVO> getRoomById(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestParam("id") Long roomId){
        return roomService.getRoomById(roomId, user.getId());
    }

    @GetMapping("/relative")
    public Response<List<SubscribedRoomVO>> listRelativeRooms(@RequestParam("room_id") Long roomId){
        return roomService.listRelativeRooms(roomId);
    }

    @GetMapping("/activation")
    public Response<Double> getRoomActivation(@RequestParam("roomId") Long roomId) {
        return roomService.getRoomActivation(roomId);
    }

    @GetMapping("/subscription/get")
    public Response<SubscribedRoomVO> getRoomSubscriptionById(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestParam("id") Long roomId){
        return roomService.getRoomById(roomId, user.getId());
    }

    @GetMapping("/index/list")
    public Response<IndexRoomListVO> listIndexRooms(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageNum", defaultValue = "20") int size) {
        return roomService.listOnlineRoomsOrderByActivation(pageNum, size);
    }

    @GetMapping("/recommend")
    public Response<List<SubscribedRoomVO>> recommendRooms(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestParam("roomId") Long roomId,
            @RequestParam(value = "count", defaultValue = "20") int count) {
        return roomService.recommendRooms(roomId, user.getId(), count);
    }

    @PostMapping("/subscribe")
    public Response<Void> subscribe(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestParam("id") Long id) {
        return roomService.subscribe(user.getId(), id);
    }

    @PostMapping("/unsubscribe")
    public Response<Void> unsubscribe(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestParam("id") Long id) {
        return roomService.unsubscribe(user.getId(), id);
    }
}
