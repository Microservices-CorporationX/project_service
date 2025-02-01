package faang.school.projectservice.dto.jira.request.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueUpdateRequestDto {

    private IssueFieldsUpdateRequestDto fields;
}
