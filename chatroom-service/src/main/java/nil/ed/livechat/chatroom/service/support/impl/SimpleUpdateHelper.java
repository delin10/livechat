package nil.ed.livechat.chatroom.service.support.impl;

import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.NormalResponseBuilder;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.common.ResponseCodeEnum;
import nil.ed.livechat.chatroom.service.support.UpdateHelper;
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
