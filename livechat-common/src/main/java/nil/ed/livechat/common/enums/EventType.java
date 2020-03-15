package nil.ed.livechat.common.enums;

import lombok.Getter;

/**
 * Created at 2020-03-05
 *
 * @author lidelin
 */

@Getter
public enum EventType implements BaseEnum{
    /**
     * 事件类型
     */
    ENTRY_ROOM(0, "进入房间"),
    EXIT_ROOM(1, "退出房间"),
    SUBSCRIBE(2, "订阅房间"),
    UNSUBSCRIBE(3,"取消订阅"),
    VERIFIED(4, "认证"),
    LOGIN(5, "登录"),
    REGISTER(6, "注册"),
    PUBLISH(7, "开播"),
    PUBLISH_DONE(8, "下播"),
    OTHER(-1, "其它");

    private int code;
    private String desc;

    static {
        EnumContext.register(EventType.class, EventType.values());
    }

    EventType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
