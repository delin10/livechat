package nil.ed.livechat.chatroom.service.support;

import java.util.List;
import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.PageResult;

public interface SelectPageHelper<T> extends Operator<PageResult<T>, List<T>>  {
    SelectPageHelper<T> setPageNo(Integer pageNo);

    SelectPageHelper<T> setPageSize(Integer pageSize);

    SelectPageHelper<T> setCounter(Supplier<Integer> counter);

}
