package faang.school.projectservice.dto.jira.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.StatusDto;
import faang.school.projectservice.dto.jira.request.AssigneeRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueFieldsResponseDto {

    @JsonProperty("issuetype")
    private IssueTypeResponseDto issueType;

    private ProjectResponseDto project;

    private StatusDto status;

    private String summary;

    private String description;

    private AssigneeRequestDto assignee;

}