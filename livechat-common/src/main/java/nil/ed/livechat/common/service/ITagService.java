package nil.ed.livechat.common.service;

import java.util.List;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.entity.TagEntity;
import nil.ed.livechat.common.vo.TagVO;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */

public interface ITagService {

    /**
     * 增加或者更新tag
     *
     * @param tag 标签
     * @return 是否更新成功
     */
    Response<Void> addTag(TagVO tag);

    /**
     * 获取所有的tags
     * @return 所有tags
     */
    Response<List<TagVO>> listAll();

    /**
     * 根据房间id获取标签列表
     * @param roomId 房间id
     * @return 标签列表
     */
    Response<List<TagVO>> listTagsByRoomId(Long roomId);

}
