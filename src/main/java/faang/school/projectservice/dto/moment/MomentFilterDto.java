package faang.school.projectservice.dto.moment;

import faang.school.projectservice.dto.project.ProjectDto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentFilterDto {
    ProjectDto project;
    LocalDateTime createdAt;

}
