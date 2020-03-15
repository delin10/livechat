package nil.ed.livechat.web.config;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.login.session.CustomSessionListener;
import nil.ed.livechat.login.session.RedisSessionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created at 2020-03-11
 *
 * @author lidelin
 */
@Slf4j
@Configuration
public class ScheduledConfig {

    @Resource
    private RedisSessionRegistry sessionRegistry;

    @Resource
    private CustomSessionListener customSessionListener;

    @Scheduled(cron = "0 5/5 * * * ?")
    public void scheduledCleanInvalidSession() {
        log.info("Start to clean invalid sessions...");
        Map<String, HttpSession> map = customSessionListener.getSessionContext();
        Set<String> sessionIds = sessionRegistry.getAllSessionIds();
        log.info("Session context: {}", map);
        log.info("Redis session center: {}", sessionIds);
        List<String> cleanedSessions = new LinkedList<>();
        sessionIds.forEach(sessionId -> {
            if (!map.containsKey(sessionId)) {
                cleanedSessions.add(sessionId);
                sessionRegistry.removeSessionInformation(sessionId);
                log.info("Removing session {}", sessionId);
            }
        });
        log.info("Session clean task complete! cleaned count = {}", cleanedSessions.size());
    }
}
