package nil.ed.livechat.chatroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author delin10
 * @since 2019/10/14
 **/
public class ChatRoomSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatRoomSpringApplication.class, args);
    }
}
