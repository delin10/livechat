package nil.ed.livechat.chatroom.stomp.interceptor;

import lombok.Getter;

/**
 * Created at 2020-01-14
 *
 * @author lidelin
 */

@Getter
public class InvalidHeaderException extends RuntimeException {

    private String name;

    private Object value;

    public InvalidHeaderException(String name, Object value) {
        super(String.format("invalid header entry [%s = %s]", name, value));
        this.name = name;
        this.value = value;
    }
}
