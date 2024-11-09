package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 128, message = "Project name must be between 3 and 128 characters")
    private String name;

    @Size(max = 4096, message = "Description must be less than 4096 characters")
    private String description;
}
