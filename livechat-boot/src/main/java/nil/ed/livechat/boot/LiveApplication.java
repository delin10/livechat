package nil.ed.livechat.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created at 2020-01-14
 *
 * @author lidelin
 */
@EnableTransactionManagement
@EnableScheduling
@MapperScan("nil.ed.livechat.common.mapper")
@ServletComponentScan(basePackages = "nil.ed.livechat.web.servlet.listener")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class}, scanBasePackages = {"nil.ed.livechat"})
public class LiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(new Class[]{ LiveApplication.class}, args);
    }
}
