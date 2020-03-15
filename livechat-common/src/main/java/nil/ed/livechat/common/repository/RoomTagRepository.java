package nil.ed.livechat.common.repository;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import nil.ed.livechat.common.entity.RoomTagEntity;
import nil.ed.livechat.common.mapper.RoomTagMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-01-18
 *
 * @author lidelin
 */

@Repository
public class RoomTagRepository {

    @Resource
    private RoomTagMapper roomTagMapper;

    public List<RoomTagEntity> listByTagId(Long tagId) {
        Example example = new Example(RoomTagEntity.class);
        example.createCriteria().andEqualTo("tagId", tagId);
        return roomTagMapper.selectByExample(example);
    }


    public List<RoomTagEntity> listByRoomId(Long roomId) {
        Example example = new Example(RoomTagEntity.class);
        example.createCriteria().andEqualTo("roomId", roomId);
        return roomTagMapper.selectByExample(example);
    }

    public void insert(RoomTagEntity entity) {
        roomTagMapper.insert(entity);
    }

    public void deleteAll(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        roomTagMapper.deleteByIds(ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }
}
