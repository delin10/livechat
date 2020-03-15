package nil.ed.livechat.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import nil.ed.livechat.common.entity.TagEntity;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagVO {

    private Long id;

    private String word;

    public TagEntity toTagEntity() {
        TagEntity entity = new TagEntity();
        entity.setId(id);
        entity.setWord(word);
        return entity;
    }

    public static TagVO parse(TagEntity entity) {
        TagVO tagVO = new TagVO();
        tagVO.setId(entity.getId());
        tagVO.setWord(entity.getWord());
        return tagVO;
    }

}
