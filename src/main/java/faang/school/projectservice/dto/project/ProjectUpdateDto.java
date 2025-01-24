package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectUpdateDto {
    @NotNull
    @Positive
    private Long id;
    @Size(max = 4096, message = "Описание проекта не должно превышать 4096 символов!")
    private String description;
    private ProjectStatus status;
}
