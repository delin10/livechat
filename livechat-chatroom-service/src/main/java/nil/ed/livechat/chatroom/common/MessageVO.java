package nil.ed.livechat.chatroom.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import nil.ed.livechat.common.vo.UserBaseVO;

/**
 * Created at 2020-03-12
 *
 * @author lidelin
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageVO {

    private String content;

    private UserBaseVO user;

}
