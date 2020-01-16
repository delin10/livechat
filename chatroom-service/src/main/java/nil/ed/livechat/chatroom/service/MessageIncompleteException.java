package nil.ed.livechat.chatroom.service;

/**
 * @author delin10
 * @since 2019/11/15
 **/
public class MessageIncompleteException extends RuntimeException{
    public MessageIncompleteException(String message) {
        super(message);
    }

    public MessageIncompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageIncompleteException(Throwable cause) {
        super(cause);
    }

    public MessageIncompleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
