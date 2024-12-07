package faang.school.projectservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private final ThreadLocal<String> sessionIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void setSessionId(String session) {
        sessionIdHolder.set(session);
    }

    public String getSessionId() {
        return sessionIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}
