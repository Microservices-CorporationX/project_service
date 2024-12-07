package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSubProjectDto {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String name;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 4096, message = "Description must be less than 4096 characters")
    private String description;

    @NotNull
    private ProjectVisibility visibility;

    @NotNull
    private ProjectStatus status;

    @NotNull
    @Min(value = 0, message = "ownerId must be non-negative")
    private Long ownerId;

}
