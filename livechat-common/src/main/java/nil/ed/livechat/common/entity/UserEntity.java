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
@Table(name = "t_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String nickname;

    private String pwd;

    private String tel;

    @Column(insertable = false)
    private String headImg;

    @Column(insertable = false)
    private String actualName;

    @Column(insertable = false)
    private String identifierId;

    @Column(insertable = false)
    private Timestamp createTime;

    @Column(insertable = false)
    private Timestamp updateTime;

}
