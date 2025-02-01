package faang.school.projectservice.dto.jira.request.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.request.AssigneeRequestDto;
import faang.school.projectservice.dto.jira.request.IssueTypeRequestDto;
import faang.school.projectservice.dto.jira.ParentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueFieldsUpdateRequestDto {

    private String summary;

    private String description;

    @JsonProperty("issuetype")
    private IssueTypeRequestDto issueType;

    @JsonProperty("parent")
    private ParentDto parentDto;

    @JsonProperty("duedate")
    private String dueDate;

    private AssigneeRequestDto assignee;

}