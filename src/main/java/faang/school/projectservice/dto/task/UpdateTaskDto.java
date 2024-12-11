package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskDto {

    @NotNull(message = "Task id can't be null")
    private Long id;

    @NotNull(message = "Task status should be set")
    private TaskStatus status;

    @NotNull(message = "Task should have executor")
    private Long performerUserId;

    @NotNull(message = "Task should have project Id")
    private Long projectId;

    private Integer minutesTracked;
    private Long parentTaskId;
    private List<Long> linkedTasksIds;
}
