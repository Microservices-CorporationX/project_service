package faang.school.projectservice.dto.moment;

import faang.school.projectservice.dto.project.ProjectDto;

import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;


@Data
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private List<ProjectDto> projects;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
