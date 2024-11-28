package faang.school.projectservice.dto.jira.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class JiraUpdateIssuePayload {

    @JsonProperty("id")
    @NotNull(message = "Timestamp cannot be null")
    @Positive(message = "Id cannot be negative")
    private Long id;

    @JsonProperty("timestamp")
    @NotNull(message = "Timestamp cannot be null")
    private Long timestamp;

    @JsonProperty("webhookEvent")
    @NotNull(message = "Webhook event cannot be null")
    private String webhookEvent;

    @JsonProperty("issue")
    @NotNull(message = "Issue cannot be null")
    private IssuePayload issue;
}
