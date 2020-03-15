package nil.ed.livechat.common.repository.example;

import java.util.Set;
import java.util.stream.Collectors;

import nil.ed.livechat.common.common.RoomStatusEnum;
import nil.ed.livechat.common.entity.RoomEntity;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-03-09
 *
 * @author lidelin
 */

public class RoomStatusExample extends Example {
    public RoomStatusExample(Set<RoomStatusEnum> status) {
        super(RoomEntity.class);
        Set<Integer> codeList = status.stream()
                .map(RoomStatusEnum::getCode)
                .map(Short::intValue).collect(Collectors.toSet());
        this.createCriteria().andIn("status", codeList);
    }
}
