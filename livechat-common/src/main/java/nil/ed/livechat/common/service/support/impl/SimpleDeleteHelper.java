package nil.ed.livechat.common.service.support.impl;

import java.util.function.Supplier;

import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeEnum;
import nil.ed.livechat.common.service.support.DeleteHelper;
import org.springframework.stereotype.Component;

@Component("simpleDeleteHelper")
public class SimpleDeleteHelper implements DeleteHelper {
    @Override
    public Response<Void> operate(Supplier<Integer> supplier) {
        Integer deleteResult = supplier.get();

        if (deleteResult == 0){
            return new NormalResponseBuilder<Void>()
                    .setCodeEnum(ResponseCodeEnum.NOT_FOUND)
                    .build();
        }

        return new NormalResponseBuilder<Void>()
                .setCodeEnum(ResponseCodeEnum.SUCCESS)
                .build();
    }
}
