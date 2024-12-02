package faang.school.projectservice.dto.jira.issue_transition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JiraIssueTransitionSetDto {

    @NotNull(message = "Transition field must be set")
    private Transition transition;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Transition {

        @NotBlank(message = "Transition id must be set")
        String id;
    }
}
