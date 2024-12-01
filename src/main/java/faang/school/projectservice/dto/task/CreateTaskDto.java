package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDto {
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters.")
    @NotBlank(message = "Name must not be empty.")
    private String name;

    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    @NotBlank(message = "Description must not be empty.")
    private String description;

    @NotNull(message = "Status must not be null")
    private TaskStatus status;

    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long performerUserId;

    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long reporterUserId;

    @Positive(message = "Id must be positive")
    private Long parentTaskId;

    @NotNull(message = "Id must not be null")
    @Positive(message = "Id must be positive")
    private Long projectId;

    @NotNull(message = "Id must not be null")
    @Positive(message = "Id must be positive")
    private Long stageId;
}
