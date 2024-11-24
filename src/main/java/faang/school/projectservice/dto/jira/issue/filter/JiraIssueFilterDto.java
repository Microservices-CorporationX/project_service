package faang.school.projectservice.dto.jira.issue.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JiraIssueFilterDto {

    private String projectKey;
    private String assigneeUsername;
    private String status;
}
