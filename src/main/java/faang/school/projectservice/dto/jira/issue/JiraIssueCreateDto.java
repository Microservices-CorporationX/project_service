package faang.school.projectservice.dto.jira.issue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class JiraIssueCreateDto extends JiraIssueBaseDto {

    @JsonDeserialize(as = Fields.class)
    private Fields fields;

    @Getter
    @Setter
    public static class Fields extends JiraIssueBaseDto.Fields {

        @NotNull(message = "Project must be specified")
        private Project project;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Project {
            @NotBlank(message = "Project key cannot be blank")
            private String key;
        }
    }
}
