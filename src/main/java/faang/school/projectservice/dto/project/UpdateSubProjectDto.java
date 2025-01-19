package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateSubProjectDto {
    private long id;
    @NotNull
    private ProjectStatus projectStatus;
    private ProjectVisibility visibility;
    private LocalDateTime updatedAt;
}
