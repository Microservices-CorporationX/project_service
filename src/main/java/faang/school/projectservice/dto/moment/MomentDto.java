package faang.school.projectservice.dto.moment;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;


@Data
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private List<Resource> resource;
    private List<Project> projects;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
