package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private Long id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must not exceed 128 characters")
    private String name;

    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    private String description;

    @NotNull(message = "OwnerId must not be null")
    private Long ownerId;
    private Long parentProjectId;
    private String createdAt;
    private String updatedAt;
    private String status;
    @NotNull(message = "Visibility must not be null")
    private String visibility;
    private String coverImageId;
    private List<Long> teamIds;
}
