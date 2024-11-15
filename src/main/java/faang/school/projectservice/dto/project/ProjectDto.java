package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    private List<Long> childrenIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}
