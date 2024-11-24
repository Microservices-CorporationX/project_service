package faang.school.projectservice.dto.jira.issue;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JiraIssueBaseDto {

    @NotNull(message = "Fields cannot be null")
    private Fields fields;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Fields {

        @NotNull(message = "Issue type must be specified")
        private IssueType issuetype;

        @NotBlank(message = "Summary cannot be blank")
        private String summary;

        @NotBlank(message = "Description cannot be blank")
        private String description;

        private Assignee assignee;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime duedate;

        private Parent parent;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class IssueType {
            @NotBlank(message = "Issue type ID cannot be blank")
            private String name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Assignee {
            @NotBlank(message = "Assignee ID cannot be blank")
            private String id;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Parent {
            @NotBlank(message = "Parent key cannot be blank")
            private String key;
        }
    }
}
