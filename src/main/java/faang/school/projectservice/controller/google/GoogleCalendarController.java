package faang.school.projectservice.controller.google;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.google.GoogleCalendarService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/calendar")
@AllArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final UserContext userContext;

    @GetMapping("/authorization")
    public String authorize(@RequestParam(name = "code") String authorizationCode, @RequestParam String state) throws IOException {
        Long userId = Long.parseLong(state.split("-")[0]);
        return googleCalendarService.authorizeUser(authorizationCode, userId);
    }

    @PostMapping("/{eventId}")
    public String createEvent(@PathVariable Long eventId) throws IOException {
        Long userId = userContext.getUserId();
        return googleCalendarService.createEvent(userId, eventId);
    }
}