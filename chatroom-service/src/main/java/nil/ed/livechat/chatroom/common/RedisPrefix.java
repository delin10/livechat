package nil.ed.livechat.chatroom.common;

import nil.ed.livechat.chatroom.util.redis.RedisConfig;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public class RedisPrefix {
    public static final String ROOM_ONLINE_COUNT_PATTERN = "room_online_count:room_id_%s";

    /**
     * 聊天室的用户发言统计
     */
    public static final String ROOM_CHAT_COUNT_OF_USER_PATTERN = "room_chat_count:room_id_%s";

    /**
     * 用户房间发言次数统计
     * user_chat_count:{roomId}
     */
    public static final String ROOM_USER_CHAT_TOTAL_COUNT = "room_user_chat_total_count:%s";

    /**
     * 用户频繁发言时锁定
     * room__user_chat_lock:{roomId}:{userId}
     */
    public static final String ROOM_USER_CHAT_LOCK = "room_user_chat_lock:%s:%s";

    /**
     * 用户聊天频度统计
     * room_user_chat_frequency:{roomId}:{userId}
     */
    public static final String ROOM_USER_CHAT_FREQUENCY = "room_user_chat_frequency:%s:%s";

    public static final RedisConfig OUT_BOUND_ECHO_CALLBACK_PUB_SUB = new RedisConfig("custom-send-echo", "自定义发送：回显连接信息");

    public static final RedisConfig GROUP_ID_KEY = new RedisConfig("consumer:group:id", "为每台机器生成一个consumer sequence");

}
