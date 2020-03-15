package nil.ed.livechat.common.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.sql.Timestamp;

import lombok.Data;

/**
 * Created at 2020-01-16
 *
 * @author lidelin
 */

@Data
@Table(name = "t_room_subscription")
public class RoomSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    private Long uid;

    private Long roomId;

    @Column(insertable = false)
    private Integer level;

    @Column(insertable = false)
    private Long watchTime;

    @Column(insertable = false)
    private Short deleteFlag;

    @Column(insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(insertable = false, updatable = false)
    private Timestamp updateTime;

}
