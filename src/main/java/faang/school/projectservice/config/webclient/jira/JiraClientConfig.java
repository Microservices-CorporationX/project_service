package faang.school.projectservice.config.webclient.jira;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Base64;

@Component
public class JiraClientConfig {

    @Value("${services.jira.protocol}")
    private String protocol;

    @Value("${services.jira.api-path}")
    private String apiPath;

    public WebClient getJiraClient(String domain, String email, String token) {
        String basicAuth = Base64.getEncoder().encodeToString((email + ":" + token).getBytes());
        String baseUrl = "%s://%s.%s".formatted(protocol, domain, apiPath);

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20_000);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
