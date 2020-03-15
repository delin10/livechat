package nil.ed.livechat.common.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInRoomInfoVO {

    private Long roomId;

    private Boolean subscribed = Boolean.FALSE;

    private Integer level;

    private Long subscribedTime;

}
