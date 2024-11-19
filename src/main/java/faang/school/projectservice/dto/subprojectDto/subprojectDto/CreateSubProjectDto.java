package faang.school.projectservice.dto.subprojectDto.subprojectDto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    Long id;
    @NotNull
    private Long parentID;
    @NotBlank
    private String name;
    @NotNull
    private String description;
    @NotNull(message = "Project status is required")
    private ProjectStatus status;
    @NotNull(message = "Project visibility is required")
    private ProjectVisibility visibility;
    @NotNull
    private Long ownerId;
    @NotNull
    private Boolean isPrivate;
    private List<Long> children;
}
