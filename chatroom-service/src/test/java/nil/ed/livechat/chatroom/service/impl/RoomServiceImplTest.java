package nil.ed.livechat.chatroom.service.impl;

import javax.annotation.Resource;

import nil.ed.livechat.chatroom.AbstractServiceTest;
import org.junit.Test;

public class RoomServiceImplTest extends AbstractServiceTest {

    @Resource
    private RoomServiceImpl roomService;

    @Test
    public void incrAndExpireSecond() {
        System.out.println(roomService.incrAndExpireSecond("test", 2L));
    }
}
