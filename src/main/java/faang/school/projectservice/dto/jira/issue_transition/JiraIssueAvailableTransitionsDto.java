package faang.school.projectservice.dto.jira.issue_transition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JiraIssueAvailableTransitionsDto {

    private List<TransitionInfo> transitions;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TransitionInfo {
        private String id;
        private String name;
    }
}
