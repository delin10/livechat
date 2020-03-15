package nil.ed.livechat.common.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class RegisterVO {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String nickname;

    @NotNull
    @NotEmpty
    private String tel;

    @NotNull
    @NotEmpty
    @Size(min = 6, message = "密码长度必须大于6")
    private String password;

    public UserEntity toUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setNickname(nickname);
        userEntity.setTel(tel);
        userEntity.setPwd(password);
        return userEntity;
    }

}
