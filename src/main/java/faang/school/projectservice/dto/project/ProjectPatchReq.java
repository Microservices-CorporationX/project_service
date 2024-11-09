package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Min;

public record ProjectPatchReq(@Min(1) Long id, String description, ProjectStatus status) {
}
