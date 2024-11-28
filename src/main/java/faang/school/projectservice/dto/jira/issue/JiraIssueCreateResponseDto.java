package faang.school.projectservice.dto.jira.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JiraIssueCreateResponseDto {

    private long id;
    private String key;

    @JsonProperty("self")
    private String apiUrl;
}
