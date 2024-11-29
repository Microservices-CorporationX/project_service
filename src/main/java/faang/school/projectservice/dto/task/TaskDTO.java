package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {

    private Long id;
    private Long parentTaskId;
    private Long projectId;
    private Long stageId;

    @Positive(message = "Идентификатор исполнителя не может быть отрицательным")
    @NotNull(message = "Идентификатор исполнителя не может быть null")
    private Long performerUserId;

    @Positive(message = "Идентификатор репортера не может быть отрицательным")
    @NotNull(message = "Идентификатор репортера не может быть null")
    private Long reporterUserId;

    private Integer minutesTracked;

    @NotNull(message = "Название задачи не может быть null")
    @Size(min = 1, max = 255, message = "Название задачи должно быть от 1 до 255 символов")
    private String name;

    @Size(max = 1000, message = "Описание задачи не может быть более 1000 символов")
    private String description;

    @NotNull(message = "Статус задачи не может быть null")
    private TaskStatus status;
}
