package nil.ed.livechat.chatroom.service.support.impl;

import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.NormalResponseBuilder;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.common.ResponseCodeEnum;
import nil.ed.livechat.chatroom.service.support.DeleteHelper;
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
