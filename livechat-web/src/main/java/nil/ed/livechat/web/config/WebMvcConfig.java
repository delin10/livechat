package nil.ed.livechat.web.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import nil.ed.livechat.login.session.CustomSessionListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created at 2020-03-10
 *
 * @author lidelin
 */

@Configuration
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
public class WebMvcConfig {

    @Bean
    public MultipartResolver multipartResolver() throws IOException {
        String tmpDir = "~/tmp";
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxInMemorySize((int)(100 * FileUtils.ONE_MB));
        resolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
        resolver.setMaxUploadSize((int)(100 * FileUtils.ONE_MB));
        File f = new File(tmpDir);
        if (!f.exists()) {
            /*
            ignore
             */
            f.mkdir();
        }
        resolver.setUploadTempDir(new FileSystemResource(tmpDir));
        return resolver;
    }

    @Bean
    public CustomSessionListener customSessionListener() {
        return new CustomSessionListener();
    }

    @Bean
    public ServletListenerRegistrationBean<CustomSessionListener> customSessionListenerBean() {
        ServletListenerRegistrationBean<CustomSessionListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(customSessionListener());
        return bean;
    }

}
