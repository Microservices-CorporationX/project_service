package faang.school.projectservice.dto.jira.issue_link;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JiraIssueLinkCreateDto {

    @NotNull(message = "Issue link type must be set")
    private Type type;

    @NotNull(message = "Inward issue must be set")
    private InwardIssue inwardIssue;

    @NotNull(message = "Outward issue must be set")
    private OutwardIssue outwardIssue;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Type {
        @NotBlank(message = "Type name must be set")
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class InwardIssue {
        @NotBlank(message = "Inward issue key must be set")
        private String key;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OutwardIssue {
        @NotBlank(message = "Outward issue key must be set")
        private String key;
    }
}
