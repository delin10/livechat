package nil.ed.livechat.common.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created at 2020-03-06
 *
 * @author lidelin
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserVerifyVO {

    private String name;

    private String identifierId;

}
