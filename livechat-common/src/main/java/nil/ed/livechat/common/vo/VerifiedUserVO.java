package nil.ed.livechat.common.vo;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nil.ed.livechat.common.entity.UserEntity;

/**
 * Created at 2020-03-05
 * 已认证的用户VO
 * @author lidelin
 */

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VerifiedUserVO extends UserBaseVO {

    private List<OwnedRoomVO> rooms;

    public static VerifiedUserVO parse(UserEntity entity, List<OwnedRoomVO> rooms) {
        VerifiedUserVO verifiedUserVO = new VerifiedUserVO();
        verifiedUserVO.setId(entity.getId());
        verifiedUserVO.setUsername(entity.getUsername());
        verifiedUserVO.setNickname(entity.getNickname());
        verifiedUserVO.setIsVerified(Boolean.TRUE);
        verifiedUserVO.setHeadImg(entity.getHeadImg());
        verifiedUserVO.setRooms(Collections.unmodifiableList(rooms));
        return verifiedUserVO;
    }
}
