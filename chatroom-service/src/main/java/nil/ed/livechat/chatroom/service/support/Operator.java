package nil.ed.livechat.chatroom.service.support;

import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.Response;

public interface Operator<T,R> {
    Response<T> operate(Supplier<R> supplier);
}
