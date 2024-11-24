package faang.school.projectservice.dto.jira.issue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JiraIssuesDto {

    private List<JiraIssueDto> issues;
}
