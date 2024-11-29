package faang.school.projectservice.controller.google.calendar;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.dto.UserDto;
import faang.school.projectservice.service.google.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/calendar")
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @GetMapping()
    public void getEvents() throws IOException {
        googleCalendarService.listEvents();
    }

    @GetMapping("/test-secure")
    public String get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        return oAuth2User.getAttributes().toString();
    }

    @GetMapping("/secure")
    public void createEvent() throws IOException {
        googleCalendarService.createEvent();
    }
}
