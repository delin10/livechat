package nil.ed.livechat.chatroom.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@Data
@Table(name = "t_room")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private Long uid;

    private String description;

    private String secretKey;

    private String title;

    private Short deleteFlag;

    private Short status;

    private Timestamp createTime;

    private Timestamp updateTime;

}
