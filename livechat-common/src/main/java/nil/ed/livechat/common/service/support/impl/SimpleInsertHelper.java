package nil.ed.livechat.common.service.support.impl;

import java.util.function.Supplier;

import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.service.support.InsertHelper;

public class SimpleInsertHelper implements InsertHelper {
    @Override
    public Response<Void> operate(Supplier<Void> supplier) {
        supplier.get();

        return new NormalResponseBuilder<Void>()
                .success(null);
    }
}
