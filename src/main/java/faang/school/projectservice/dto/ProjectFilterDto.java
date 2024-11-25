package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectFilterDto {

    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Status must not be null")
    private ProjectStatus status;
}
