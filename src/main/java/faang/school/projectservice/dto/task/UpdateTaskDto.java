package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskDto {
    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotBlank(message = "Name must not be empty.")
    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long performerUserId;

    @NotNull(message = "Status must not be null")
    private TaskStatus status;

    @Positive(message = "Id must be positive")
    private Long parentTaskId;
}
