package faang.school.projectservice.publisher.TaskCompletedEvent;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCompletedEvent {
    @NotNull
    private Long projectId;
    @NotNull
    private Long userId;
    @NotNull
    private Long taskId;
    @NotNull
    private TaskStatus taskStatus;
}