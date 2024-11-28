package faang.school.projectservice.exceptions.jira;

import org.springframework.web.reactive.function.client.ClientResponse;

public class JiraNotFoundException extends RuntimeException {


    public JiraNotFoundException(String message) {
        super(message);
    }

    public JiraNotFoundException(ClientResponse clientResponse) {
        super(String.format("Jira not found error: %s", clientResponse));
    }
}
