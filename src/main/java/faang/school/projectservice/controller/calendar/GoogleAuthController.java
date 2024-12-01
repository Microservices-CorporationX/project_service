package faang.school.projectservice.controller.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import faang.school.projectservice.service.calendar.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleClientSecrets clientSecrets;
    @Value("${google.redirect-uri}")
    private String redirectUri;
    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/oauth2/google")
    public String initiateGoogleAuth() {
        String authUrl = clientSecrets.getDetails().getAuthUri()
                + "?client_id=" + clientSecrets.getDetails().getClientId()
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode("https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.events", StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&access_type=offline"
                + "&approval_prompt=force";

        return "Перейдите по <a href='" + authUrl + "'>ссылке</a> для авторизации через Google.";
    }

    @GetMapping("/oauth2/callback")
    public void googleCallback(@RequestParam("code") String code) {
        googleCalendarService.getAndSaveToken(code);
    }
}
