package nil.ed.livechat.login.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Created at 2020-03-11
 *
 * @author lidelin
 */
@Slf4j
public class CustomSessionListener implements HttpSessionListener, HttpSessionIdListener {

    private Map<String, HttpSession> sessionContext = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (log.isDebugEnabled()) {
            log.info("Create session: sessionId = {}", se.getSession().getId());
            log.info("Session context = {}", sessionContext);
        }
        sessionContext.put(se.getSession().getId(), se.getSession());
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        onCreated(sessionId, session, se);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        if (log.isDebugEnabled()) {
            log.info("Destroy session: sessionId = {}", se.getSession().getId());
            log.info("Session context = {}", sessionContext);
        }
        sessionContext.remove(se.getSession().getId());
        onDestroyed(sessionId, session, se);
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent se, String oldSessionId) {
        HttpSession session = se.getSession();
        if (log.isDebugEnabled()) {
            log.info("Session id changed! old = {}, new = {}", oldSessionId, session.getId());
        }
        sessionContext.remove(oldSessionId);
        sessionContext.put(session.getId(), session);
        if (log.isDebugEnabled()) {
            log.info("Session context = {}", sessionContext);
        }
    }

    public Map<String, HttpSession> getSessionContext() {
        return sessionContext;
    }

    /**
     * 会话销毁回调
     *
     * @param sessionId 会话id
     * @param session  会话
     * @param se 事件
     */
    protected void onDestroyed(String sessionId, HttpSession session, HttpSessionEvent se){

    }

    /**
     * 会话创建回调
     *
     * @param sessionId 会话id
     * @param session  会话
     * @param se 事件
     */
    protected void onCreated(String sessionId, HttpSession session, HttpSessionEvent se){

    }
}
