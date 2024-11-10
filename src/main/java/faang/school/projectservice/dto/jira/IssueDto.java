package faang.school.projectservice.dto.jira;

import jakarta.validation.constraints.Future;
import lombok.Builder;
import lombok.NonNull;
import org.joda.time.LocalDateTime;

@Builder
public record IssueDto(String issueKey,
                       long issueTypeId,
                       @NonNull
                       String summary,
                       String description,
                       String assignee,
                       @NonNull
                       @Future
                       LocalDateTime deadline) {
}