package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubProjectDto {

    @NotNull(message = "Id must not be empty")
    @Positive(message = "Id must be positive integer")
    private Long id;

    private ProjectStatus status;
    private ProjectVisibility visibility;
}
