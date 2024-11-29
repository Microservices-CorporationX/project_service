package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskFiltersDto {
    private TaskStatus status;
    private String text;
    private Long performerUserId;
}
