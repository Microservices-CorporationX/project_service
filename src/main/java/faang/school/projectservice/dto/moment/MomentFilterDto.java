package faang.school.projectservice.dto.moment;

import faang.school.projectservice.model.Project;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentFilterDto {
    Project project;
    LocalDateTime createdAt;

}
