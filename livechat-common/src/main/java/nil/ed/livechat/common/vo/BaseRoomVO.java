package nil.ed.livechat.common.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created at 2020-03-10
 *
 * @author lidelin
 */

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRoomVO {

    private Long id;

    private String title;

    private String cover;

    private String description;

    private Short status;

    private List<TagVO> tags;

    private Double activation;

    private Integer subscriptionCount;

}
