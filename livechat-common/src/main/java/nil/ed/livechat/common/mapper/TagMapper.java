package nil.ed.livechat.common.mapper;

import nil.ed.livechat.common.entity.TagEntity;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created at 2020-01-16
 *
 * @author lidelin
 */

public interface TagMapper extends Mapper<TagEntity>, IdsMapper<TagEntity> {
}
