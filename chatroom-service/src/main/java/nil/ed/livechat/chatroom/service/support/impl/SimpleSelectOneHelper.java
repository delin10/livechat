package nil.ed.livechat.chatroom.service.support.impl;

import java.util.Objects;
import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.NormalResponseBuilder;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.common.ResponseCodeEnum;
import nil.ed.livechat.chatroom.service.support.SelectOneHelper;

public class SimpleSelectOneHelper<T> implements SelectOneHelper<T> {
    @Override
    public Response<T> operate(Supplier<T> supplier) {
        T result = supplier.get();

        if (Objects.isNull(result)){
            return new NormalResponseBuilder<T>()
                    .setCodeEnum(ResponseCodeEnum.NOT_FOUND)
                    .build();
        }
        return new NormalResponseBuilder<T>()
                .setData(result)
                .setCodeEnum(ResponseCodeEnum.SUCCESS)
                .build();
    }
}
