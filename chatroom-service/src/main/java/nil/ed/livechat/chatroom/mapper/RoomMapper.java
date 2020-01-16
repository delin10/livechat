package nil.ed.livechat.chatroom.mapper;

import nil.ed.livechat.chatroom.entity.RoomEntity;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@Repository("roomMapper")
public interface RoomMapper extends Mapper<RoomEntity> {
}
