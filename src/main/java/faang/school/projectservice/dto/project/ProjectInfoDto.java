package faang.school.projectservice.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectInfoDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
}
