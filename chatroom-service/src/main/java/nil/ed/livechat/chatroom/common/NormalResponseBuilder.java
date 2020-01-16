package nil.ed.livechat.chatroom.common;

/**
 * builder for normal response
 * @param <T> data type
 */
public class NormalResponseBuilder<T> extends ResponseBuilder<T> {
    @Override
    public Response<T> build() {
        Response<T> response = new Response<>();
        response.setData(data);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
