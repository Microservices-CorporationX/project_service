package ru.corporationx.projectservice.controller.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import ru.corporationx.projectservice.service.calendar.GoogleCalendarApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("google-oauth")
public class GoogleAuthController {
    private final GoogleClientSecrets clientSecrets;
    private final GoogleCalendarApiService googleAuthService;
    @Value("${google.redirect-uri}")
    private String redirectUri;

    @GetMapping
    public String initiateGoogleAuth() {
        String authUrl = googleAuthService.getAuthUrl();
        log.info(authUrl);
        return "Перейдите по <a href='" + authUrl + "'>ссылке</a> для авторизации через Google.";
    }

    @GetMapping("/callback")
    public void googleCallback(@RequestParam("code") String code) {
        googleAuthService.acquireToken(code);
    }
}
