package faang.school.projectservice.dto.jira.request.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueCreateRequestDto {

    private IssueFieldsCreateRequestDto fields;
}
