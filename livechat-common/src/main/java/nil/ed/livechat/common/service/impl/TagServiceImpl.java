package nil.ed.livechat.common.service.impl;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.entity.RoomTagEntity;
import nil.ed.livechat.common.entity.TagEntity;
import nil.ed.livechat.common.repository.RoomTagRepository;
import nil.ed.livechat.common.repository.TagRepository;
import nil.ed.livechat.common.service.ITagService;
import nil.ed.livechat.common.service.support.impl.SimpleInsertHelper;
import nil.ed.livechat.common.service.support.impl.SimpleSelectOneHelper;
import nil.ed.livechat.common.vo.TagVO;
import org.springframework.stereotype.Service;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */

@Service
public class TagServiceImpl implements ITagService {

    @Resource
    private TagRepository tagRepository;

    @Resource
    private RoomTagRepository roomTagRepository;

    @Override
    public Response<Void> addTag(TagVO tag) {
        return new SimpleInsertHelper().operate(() -> {
            tagRepository.insert(tag.toTagEntity());
            return null;
        });
    }

    @Override
    public Response<List<TagVO>> listAll() {
        return new SimpleSelectOneHelper<List<TagVO>>()
        .operate(() -> toTagVOBatch(tagRepository.listAll()));
    }

    @Override
    public Response<List<TagVO>> listTagsByRoomId(Long roomId) {
        return new SimpleSelectOneHelper<List<TagVO>>()
                .operate(() -> {
                    List<Long> ids = roomTagRepository.listByRoomId(roomId)
                            .stream()
                            .map(RoomTagEntity::getTagId)
                            .collect(Collectors.toList());
                    return toTagVOBatch(tagRepository.listTags(ids));
                });
    }

    public List<TagVO> toTagVOBatch(List<TagEntity> entities) {
        return entities.stream()
                .map(TagVO::parse)
                .collect(Collectors.toList());
    }
}
