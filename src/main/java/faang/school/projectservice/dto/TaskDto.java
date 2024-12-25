package faang.school.projectservice.dto;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must not exceed 128 characters")
    private String name;

    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Task status must not be null")
    private TaskStatus status;

    private Long performerUserId;

    private Long reporterUserId;
}
