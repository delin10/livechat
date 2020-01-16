package nil.ed.livechat.chatroom.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author delin10
 * @since 2019/10/14
 **/
@Data
@Table(name = "t_user")
public class UserEntity extends TimeRecordEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
