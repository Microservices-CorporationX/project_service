package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateSubProjectDto {

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

}
