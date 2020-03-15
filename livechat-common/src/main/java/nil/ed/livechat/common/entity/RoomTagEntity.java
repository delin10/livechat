package nil.ed.livechat.common.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Created at 2020-01-18
 *
 * @author lidelin
 */

@Data
@Table(name = "t_room_tag")
public class RoomTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    private Long roomId;

    private Long tagId;

}
