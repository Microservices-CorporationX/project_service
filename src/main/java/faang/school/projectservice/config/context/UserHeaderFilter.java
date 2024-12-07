package faang.school.projectservice.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getHeader("x-user-id");

        if (userId != null) {
            userContext.setUserId(Long.parseLong(userId));
        }

        Optional<String> sessionId = Arrays.stream(req.getCookies()).sequential()
                .filter(cookie -> cookie.getName().equals("SESSION"))
                .map(Cookie::getValue)
                .findFirst();

        sessionId.ifPresent(userContext::setSessionId);
        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
