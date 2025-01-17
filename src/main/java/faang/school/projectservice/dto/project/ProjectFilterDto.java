package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectFilterDto {
    @NotNull
    private Long id;
    private String namePattern;
    @NotNull
    private ProjectStatus statusPattern;
}
