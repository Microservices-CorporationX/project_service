package faang.school.projectservice.dto.subprojectDto.subprojectDto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private ProjectStatus status;
    @NotNull
    private ProjectVisibility visibility;
    @NotNull
    private Long ownerId;
    private List<Long> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
