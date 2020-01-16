package nil.ed.livechat.chatroom.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created at 2020-01-14
 *
 * @author lidelin
 */

@Getter
public enum RoomStatusEnum {
    /**
     * 房间状态
     */
    OFFLINE((short)0, "下线"),
    ONLINE((short)1, "在线"),
    FROZEN((short)2, "被冻结中");

    private Short code;

    private String desc;

    RoomStatusEnum(Short code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
