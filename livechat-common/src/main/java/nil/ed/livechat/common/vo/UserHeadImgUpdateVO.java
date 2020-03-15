package nil.ed.livechat.common.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nil.ed.livechat.common.entity.UserEntity;

/**
 * Created at 2020-03-06
 *
 * @author lidelin
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserHeadImgUpdateVO {
    private String headImg;

    public UserEntity toUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setHeadImg(headImg);
        return entity;
    }
}
