package faang.school.projectservice.dto.jira;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {
    private String projectKey;
    private IssueTypeDto issueType;
    @NotBlank(message = "Summary is required")
    private String summary;
    @NotBlank(message = "Description is required")
    private String description;
}
