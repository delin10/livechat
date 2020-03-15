package nil.ed.livechat.common.service.support.impl;

import java.util.function.Supplier;

import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeEnum;
import nil.ed.livechat.common.service.support.UpdateHelper;
import org.springframework.stereotype.Component;

@Component("simpleUpdateHelper")
public class SimpleUpdateHelper implements UpdateHelper {
    @Override
    public Response<Void> operate(Supplier<Integer> supplier)  {
        Integer updateResult = supplier.get();

        if (updateResult == 0){
            return new NormalResponseBuilder<Void>()
                    .setCodeEnum(ResponseCodeEnum.NOT_FOUND)
                    .build();
        }

        return new NormalResponseBuilder<Void>()
                .setCodeEnum(ResponseCodeEnum.SUCCESS)
                .build();
    }
}
