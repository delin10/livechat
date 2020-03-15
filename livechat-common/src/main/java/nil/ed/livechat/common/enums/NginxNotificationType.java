package nil.ed.livechat.common.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created at 2019-12-12
 *
 * @author lidelin
 */

public enum NginxNotificationType {
    /**
     * All notification type of nginx_rtmp
     */
    PUBLISH("publish", "notify when video is published"),
    PUBLISH_DONE("publish_done", "notify when publish terminate"),
    PLAY("play", "notify when video is played by client"),
    PLAY_DONE("play_done", "notify when client play terminate"),
    RECORD_DONE("record_done", "notify when video record completed"),
    DONE("done", "notify when play or publish terminate"),
    /**
     * The following two are concrete type of update event
     */
    UPDATE_PUBLISH("update_publish", "notify when video file is refreshed"),
    UPDATE_PLAY("update_play", "notify when play is updated(default with the interval 30s)"),
    CONNECT("connect", "client connect event");

    private static Map<String, NginxNotificationType> indexMapper = new HashMap<>(8, 1);

    static {
        Arrays.stream(NginxNotificationType.values())
                .forEach(type -> indexMapper.put(type.callText, type));
    }

    private final String callText;

    private final String description;

    NginxNotificationType(String callText, String description) {
        this.callText = callText;
        this.description = description;
    }

    public String getCallText() {
        return callText;
    }

    public String getDescription() {
        return description;
    }

    public static NginxNotificationType getTypeByCallText(String callText) {
        return indexMapper.get(callText);
    }
}
