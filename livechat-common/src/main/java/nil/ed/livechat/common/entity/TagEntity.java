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
@Table(name = "t_tag")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    @Column(insertable = false)
    private Short deleteFlag;

    @Column(insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(insertable = false, updatable = false)
    private Timestamp updateTime;

}
