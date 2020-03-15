package nil.ed.livechat.common.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author delin10
 * @since 2019/10/22
 **/
@Getter
@Setter
public class UserChatRecordVO implements Serializable {
    private String username;

    /*
    发言次数统计
     */
    private Long chatCount;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return username != null && username.equals(obj);
        }
        return false;
    }
}
