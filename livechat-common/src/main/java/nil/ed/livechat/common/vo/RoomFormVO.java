package nil.ed.livechat.common.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.validator.annotation.UpdateValidatedGroup;

/**
 * Created at 2020-03-11
 *
 * @author lidelin
 */

@Getter
@Setter
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoomFormVO {

    @NotNull(groups = UpdateValidatedGroup.class)
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private String cover;

    @NotNull
    @NotEmpty
    private Set<Long> tags;

    public RoomEntity toRoomEntity() {
        RoomEntity entity = new RoomEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setCover(cover);
        return entity;
    }

}
