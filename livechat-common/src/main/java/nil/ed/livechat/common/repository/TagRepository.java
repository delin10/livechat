package nil.ed.livechat.common.repository;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import nil.ed.livechat.common.entity.TagEntity;
import nil.ed.livechat.common.mapper.TagMapper;
import org.springframework.stereotype.Repository;

/**
 * Created at 2020-03-12
 *
 * @author lidelin
 */

@Repository
public class TagRepository {

    @Resource
    private TagMapper tagMapper;

    public List<TagEntity> listTags(List<Long> ids) {
        return tagMapper.selectByIds(ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }

    public void insert(TagEntity tagEntity) {
        tagMapper.insert(tagEntity);
    }

    public List<TagEntity> listAll() {
        return tagMapper.selectAll();
    }

    public boolean checkExists(Long id) {
        return tagMapper.existsWithPrimaryKey(id);
    }

}
