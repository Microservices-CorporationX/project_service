package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFilterDto {
    @NotBlank(message = "Name must not be empty.")
    @Size(min = 3, max = 128, message = "Name must be between 3 and 128 characters.")
    private String name;
    private ProjectStatus status;
}
