package nil.ed.livechat.common.entity;

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
@Table(name = "t_user_log")
public class UserLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long uid;

    private Short eventCode;

    private String remark;

    private Timestamp createTime;

}
