package nil.ed.livechat.common.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nil.ed.livechat.common.entity.RoomEntity;

/**
 * Created at 2020-01-16
 *
 * @author lidelin
 */

@Getter
@Setter
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscribedRoomVO extends BaseRoomVO{

    private UserBaseVO user;

    private Boolean isSubscribed;

    public static SubscribedRoomVO parse(RoomEntity entity, UserBaseVO user, List<TagVO> tags) {
        SubscribedRoomVO subscribedRoomVO = new SubscribedRoomVO();
        subscribedRoomVO.setId(entity.getId());
        subscribedRoomVO.setUser(user);
        subscribedRoomVO.setTitle(entity.getTitle());
        subscribedRoomVO.setDescription(entity.getDescription());
        subscribedRoomVO.setStatus(entity.getStatus());
        subscribedRoomVO.setCover(entity.getCover());
        subscribedRoomVO.setTags(tags);
        return subscribedRoomVO;
    }

}
