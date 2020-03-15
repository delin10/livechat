package nil.ed.livechat.common.common;

import nil.ed.livechat.common.util.redis.RedisConfig;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public class RedisPrefix {

    /**
     * STOMP
     */

    public static final RedisConfig STOMP_SESSION_ROOM_MAP= new RedisConfig("stomp:session:room:map", "   stomp会话房间映射:key -> {stompSessionId:roomId}");

    /**
     * 数据统计
     */
    public static final RedisConfig ROOM_CHAT_ONLINE_COUNT = new RedisConfig("room:chat:online:count:%s", "房间聊天在线人数统计");

    /**
     * 频次控制
     */
    public static final RedisConfig ROOM_FREQUENCY_CONTROL_LOCK = new RedisConfig("message:frequency:control:lock:%s:%s", "房间频次控制：锁");

    public static final RedisConfig ROOM_FREQUENCY_CONTROL_RECORD = new RedisConfig("message:frequency:control:record:%s:%s", "房间频次控制：用户消息事件记录");

    /**
     * 活跃度计算
     */
    public static final RedisConfig ROOM_MESSAGE_COUNT = new RedisConfig("room:message:count:%s", "房间消息:总数统计");

    public static final RedisConfig ROOM_AUDIENCE_MAP = new RedisConfig("room:audience:map:%s", "房间消息:<user, messageCount>");

    public static final RedisConfig ROOM_RANK_LIST = new RedisConfig("room:rank:list:by:activation", "房间活跃度排行榜");

    public static final RedisConfig OUT_BOUND_ECHO_CALLBACK_PUB_SUB = new RedisConfig("custom-send-echo", "自定义发送：回显连接信息");

    public static final RedisConfig GROUP_ID_KEY = new RedisConfig("consumer:group:id", "为每台机器生成一个consumer sequence");

    /**
     * 记录上次刷新推流的时间戳，unit：秒
     */
    public static final RedisConfig ROOM_LAST_PUB_TIMESTAMP = new RedisConfig("room:last:update:publish:ts:%s", "用户上次的直播发布的时间");
}
