package nil.ed.livechat.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created at 2020-01-14
 *
 * @author lidelin
 */
@EnableScheduling
@MapperScan("nil.ed.livechat.chatroom.mapper")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"nil.ed.livechat"})
public class LiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(new Class[]{ LiveApplication.class}, args);
    }
}
