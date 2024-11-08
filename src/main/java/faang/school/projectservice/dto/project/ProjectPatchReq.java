package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProjectPatchReq {
    @Min(1)
    private Long id;
    private String description;
    private ProjectStatus status;
}
