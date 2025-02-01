package faang.school.projectservice.dto.jira.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeResponseDto {

    private String displayName;

    private String accountId;
}