package nil.ed.livechat.chatroom.controller;

import javax.annotation.Resource;
import java.util.List;

import nil.ed.livechat.chatroom.common.PageResult;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.entity.RoomEntity;
import nil.ed.livechat.chatroom.service.IRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@RestController
@RequestMapping("/chatroom")
public class RoomController {
    @Resource(name = "roomService")
    private IRoomService roomService;

    @PostMapping("/room/add")
    public Response<Void> addRoom(@RequestBody(required = false) RoomEntity room){
        room.setUid(1L);
        return roomService.addRoom(room);
    }

    @PostMapping("/room/delete/{roomId}")
    public Response<Void> deleteRoom(@PathVariable("roomId") Long roomId){
        return roomService.deleteRoom(roomId, 1L);
    }

    @GetMapping("/room/list")
    public Response<PageResult<RoomEntity>> listRooms(){
        return roomService.listRooms();
    }

    @GetMapping("/room/{roomId}/user/list/rank")
    public Response<List<Object>> listRooms(@PathVariable("roomId") Long roomId){
        return roomService.listRankUsersTop50(roomId);
    }

    public Response<Integer> getRoomActivation(@RequestParam("roomId") Long roomId) {
        return roomService.getRoomActivation(roomId);
    }

}
