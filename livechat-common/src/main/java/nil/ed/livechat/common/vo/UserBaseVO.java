package nil.ed.livechat.common.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nil.ed.livechat.common.entity.UserEntity;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserBaseVO {

    private Long id;

    private String username;

    private String nickname;

    private Boolean isVerified;

    private String headImg;

    public static UserBaseVO parse(UserEntity entity) {
        UserBaseVO userBaseVO = new UserBaseVO();
        userBaseVO.setId(entity.getId());
        userBaseVO.setUsername(entity.getUsername());
        userBaseVO.setNickname(entity.getNickname());
        userBaseVO.setIsVerified(Boolean.FALSE);
        userBaseVO.setHeadImg(entity.getHeadImg());
        return userBaseVO;
    }

}
