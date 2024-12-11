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
public class CreateTaskDto {

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotNull
    private TaskStatus status;

    @NotNull
    private Long performerUserId;

    @NotNull
    private Long reporterUserId;

    private Long parentTaskId;
    private List<Long> linkedTasksIds;

    @NotNull
    private Long projectId;

    private Long stageId;
}
