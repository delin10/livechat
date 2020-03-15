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
 * Created at 2020-03-10
 *
 * @author lidelin
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnedRoomVO extends BaseRoomVO {

    private String secretKey;

    public static OwnedRoomVO parse(RoomEntity entity) {
        OwnedRoomVO ownedRoomVO = new OwnedRoomVO();
        ownedRoomVO.setId(entity.getId());
        ownedRoomVO.setTitle(entity.getTitle());
        ownedRoomVO.setDescription(entity.getDescription());
        ownedRoomVO.setStatus(entity.getStatus());
        ownedRoomVO.setCover(entity.getCover());
        ownedRoomVO.setSecretKey(entity.getSecretKey());
        return ownedRoomVO;
    }

}
