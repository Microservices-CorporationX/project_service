package faang.school.projectservice.config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        try {
            return userIdHolder.get();
        } catch (Exception e) {
            log.error("Missing parameter in request headers: x-user-id");
            throw new IllegalArgumentException("Missing parameter in request headers: x-user-id");
        }
    }

    public void clear() {
        userIdHolder.remove();
    }
}
