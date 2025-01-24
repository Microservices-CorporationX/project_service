package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectCreateDto {
    @NotNull
    @Positive
    private Long id;
    @NotBlank(message = "Название проекта не должно быть пустым!")
    @Size(max = 128, message = "Название проекта не должно превышать 128 символов!")
    private String name;
    @NotBlank(message = "Описание проекта не должно быть пустым!")
    @Size(max = 4096, message = "Описание проекта не должно превышать 4096 символов!")
    private String description;
    @Positive
    @NotNull
    private Long ownerId;
    private ProjectStatus status;
}
