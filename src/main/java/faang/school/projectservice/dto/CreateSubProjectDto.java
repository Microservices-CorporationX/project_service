package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
