package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSubProjectDto {
    private String name;
    private long ownerId;
    private long parentProjectId;
    private ProjectVisibility projectVisibility;
}
