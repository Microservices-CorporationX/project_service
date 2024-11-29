package faang.school.projectservice.controller;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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

    @GetMapping("/oauth2/google")
    public String initiateGoogleAuth() {
        String authUrl = clientSecrets.getDetails().getAuthUri()
                + "?client_id=" + clientSecrets.getDetails().getClientId()
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode("https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.events", StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&access_type=offline";

        return "Перейдите по <a href='" + authUrl + "'>ссылке</a> для авторизации через Google.";
    }

    @GetMapping("/oauth2/google/callback")
    public String googleCallback(@RequestParam("code") String code) {
        try {
            TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets.getDetails().getTokenUri(),
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    code,
                    redirectUri
            ).execute();

            String accessToken = tokenResponse.getAccessToken();
            return "Access Token: " + accessToken;
        } catch (Exception e) {
            return "Error exchanging code for token: " + e.getMessage();
        }
    }
}
