package faang.school.projectservice.dto.client.subprojectdto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class SubProjectFilterDto {
    private String name;
    private ProjectStatus status;
}
