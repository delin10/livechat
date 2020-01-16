package nil.ed.livechat.chatroom.service.support.impl;

import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.NormalResponseBuilder;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.service.support.InsertHelper;

public class SimpleInsertHelper implements InsertHelper {
    @Override
    public Response<Void> operate(Supplier<Void> supplier) {
        supplier.get();

        return new NormalResponseBuilder<Void>()
                .success(null);
    }
}
