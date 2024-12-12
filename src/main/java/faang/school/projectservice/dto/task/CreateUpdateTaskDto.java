package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateTaskDto {

    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotNull(message = "Task status should be set")
    private TaskStatus status;

    @NotNull(message = "Task should have executor")
    private Long performerUserId;

    @NotNull
    private Long reporterUserId;

    @NotNull(message = "Task should have project Id")
    private Long projectId;

    private Integer minutesTracked;
    private Long parentTaskId;
    private List<Long> linkedTasksIds;
    private Long stageId;
}