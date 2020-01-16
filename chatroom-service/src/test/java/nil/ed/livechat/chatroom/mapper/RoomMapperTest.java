package nil.ed.livechat.chatroom.mapper;

import javax.annotation.Resource;

import nil.ed.livechat.chatroom.AbstractServiceTest;
import nil.ed.livechat.chatroom.entity.RoomEntity;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;

/**
 * @author delin10
 * @since 2019/10/15
 **/
public class RoomMapperTest extends AbstractServiceTest {
    @Resource(name = "roomMapper")
    private RoomMapper roomMapper;

    @Test
    public void deleteByExampleTest(){
        Example example = new Example(RoomEntity.class);
        example.createCriteria().andEqualTo("id", 1);
        Integer count = roomMapper.deleteByExample(example);
        System.out.println("删除的条数为:" + count);
    }

}
