package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    @NotNull
    @Size(max = 128)
    private String name;

    @NotNull
    @Size(max = 4096)
    private String description;

    @NotNull
    private ProjectVisibility visibility;

    @NotNull
    private ProjectStatus status;

    @NotNull
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenIds;
    private List<Long> teamsIds;
}
