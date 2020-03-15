package nil.ed.livechat.login.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.aop.annotation.MethodInvokeLog;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * Created at 2020-03-06
 *
 * @author lidelin
 */

@Slf4j
public class RedisSessionRegistry implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

    private static final String INFO_STR_NOT_NULL = "info str cannot be null!";

    /**
     * 存储格式: map
     *
     * sessionRegistryKey -> <sessionId, username:lastUpdateMillis>
     */
    private String sessionRegistryKey = "session:registry:map";

    /**
     * 存储格式：KV
     *
     *  session:principal:{username} ->  principal_json_str
     */
    private String principalKey = "session:principal:%s";

    /**
     * 存储格式：list
     *
     * session:principal:sessionId:{username} -> sessionId
     */
    private String principalSessionKey = "session:principal:sessionId:%s";

    private String defaultDelimiter = ":";

    private ValueOperations<String, String> valueOperations;

    private SetOperations<String, String> setOperationsForPrincipal;

    private HashOperations<String, String, String> hashOperationForSession;

    public RedisSessionRegistry(StringRedisTemplate redisTemplate) {
        this.hashOperationForSession = redisTemplate.opsForHash();
        this.setOperationsForPrincipal = redisTemplate.opsForSet();
        this.valueOperations = redisTemplate.opsForValue();
    }

    public String getSessionRegistryKey() {
        return sessionRegistryKey;
    }

    public void setSessionRegistryKey(String sessionRegistryKey) {
        this.sessionRegistryKey = sessionRegistryKey;
    }

    @Override
    @MethodInvokeLog
    public List<Object> getAllPrincipals() {
        List<Object> ls = new LinkedList<>();
        hashOperationForSession.entries(sessionRegistryKey).forEach((k, v) ->{
            String username = v.substring(0, v.indexOf(defaultDelimiter));
            String rt = valueOperations.get(String.format(principalKey, username));
            /*
             处理异常情况
             */
            if (StringUtils.isBlank(rt)) {
                hashOperationForSession.delete(sessionRegistryKey, k);
                setOperationsForPrincipal.getOperations().unlink(String.format(principalSessionKey, username));
            } else {
                ls.add(JSON.parseObject(rt, CustomUserDetailsImpl.class));
            }
        });
        return ls;
    }


    @Override
    @MethodInvokeLog
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        UserDetails userDetails = (UserDetails) principal;
        String key = String.format(this.principalSessionKey, userDetails.getUsername());
        Set<String> sessionIds = setOperationsForPrincipal.members(key);
        if (sessionIds == null) {
            return Collections.emptyList();
        }
        List<String> invalidList = new LinkedList<>();
        List<SessionInformation> sessionInformationList = new ArrayList<>(sessionIds.size());
        sessionIds.forEach(sessionId -> {
            SessionInformation information = getSessionInformation(sessionId);
            if (information == null) {
                invalidList.add(sessionId);
            } else {
                sessionInformationList.add(information);
            }
        });
        CompletableFuture.runAsync(() -> setOperationsForPrincipal.remove(key, invalidList.toArray()));
        return sessionInformationList;
    }

    @Override
    @MethodInvokeLog
    public SessionInformation getSessionInformation(String sessionId) {
        String infoStr = hashOperationForSession.get(sessionRegistryKey, sessionId);
        if (infoStr == null) {
            return null;
        }
        String[] splitArr = infoStr.split(defaultDelimiter);
        long lastRequestMillis = Long.parseLong(splitArr[1]);
        return new SessionInformation(splitArr[0], sessionId,
                new Date(lastRequestMillis));
    }

    @Override
    @MethodInvokeLog
    public void refreshLastRequest(String sessionId) {
        String infoStr = hashOperationForSession.get(sessionRegistryKey, sessionId);
        Assert.notNull(infoStr, INFO_STR_NOT_NULL);
        String[] splitArr = infoStr.split(defaultDelimiter);
        splitArr[1] = String.valueOf(System.currentTimeMillis());
        hashOperationForSession.put(sessionRegistryKey, sessionId, String.join(defaultDelimiter, splitArr));
    }

    @Override
    @MethodInvokeLog
    public void registerNewSession(String sessionId, Object principal) {
        UserDetails userDetails = (UserDetails) principal;
        valueOperations.set(String.format(principalKey, userDetails.getUsername()), JSON.toJSONString(principal));
        setOperationsForPrincipal.add(String.format(principalSessionKey, userDetails.getUsername()), sessionId);
        hashOperationForSession.put(sessionRegistryKey, sessionId, userDetails.getUsername() + defaultDelimiter + System.currentTimeMillis());
    }

    @Override
    @MethodInvokeLog
    public void removeSessionInformation(String sessionId) {
        String infoStr = hashOperationForSession.get(sessionRegistryKey, sessionId);
        Assert.notNull(infoStr, INFO_STR_NOT_NULL);
        String[] splitArr = infoStr.split(defaultDelimiter);
        CompletableFuture.runAsync(() -> {
            hashOperationForSession.delete(sessionRegistryKey, sessionId);
            valueOperations.getOperations().delete(String.format(principalKey, splitArr[0]));
            setOperationsForPrincipal.remove(String.format(principalSessionKey, splitArr[0]), sessionId);
        });
    }

    public Set<String> getAllSessionIds() {
        return hashOperationForSession.keys(sessionRegistryKey);
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        String sessionId = event.getId();
        if (log.isDebugEnabled()) {
            log.debug("Destroying session = {}", sessionId);
        }
        removeSessionInformation(sessionId);
    }
}
