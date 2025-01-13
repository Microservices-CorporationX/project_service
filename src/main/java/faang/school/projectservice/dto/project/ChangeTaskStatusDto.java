package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangeTaskStatusDto {
    @NotNull
    private Long projectId;
    @NotNull
    private Long taskId;
    @NotNull
    private TaskStatus taskStatus;
}