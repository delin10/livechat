package nil.ed.livechat.common.service.support;

import java.util.function.Supplier;

import nil.ed.livechat.common.common.Response;

public interface Operator<T,R> {
    Response<T> operate(Supplier<R> supplier);
}
