package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectUpdateRequestDto extends ProjectRequestDto {
    @Positive
    private Long id;
    @NotNull
    private ProjectStatus status;
}
