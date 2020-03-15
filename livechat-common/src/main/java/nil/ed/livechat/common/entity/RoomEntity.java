package nil.ed.livechat.common.entity;

import javax.persistence.Column;
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
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column(updatable = false)
    private Long uid;

    @Column(updatable = false)
    private String secretKey;

    private String title;

    private String description;

    @Column(insertable = false)
    private Short deleteFlag;

    @Column(insertable = false)
    private Short status;

    private String cover;

    @Column(insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(insertable = false, updatable = false)
    private Timestamp updateTime;

}
