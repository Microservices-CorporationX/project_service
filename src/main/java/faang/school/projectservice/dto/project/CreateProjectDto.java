package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class CreateProjectDto {

    @NotBlank(message = "Project name must not be empty")
    @Size(max = 128, message = "Project name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Description name must not be empty")
    @Size(max = 4096, message = "Description must be between 10 and 4096 characters")
    private String description;

    @NotNull(message = "Storage size must not be empty")
    private BigInteger storageSize;

    @NotNull(message = "Max storage size must not be empty")
    private BigInteger maxStorageSize;

    @NotNull(message = "Owner id must not be empty")
    @Positive(message = "Owner id must be positive")
    private Long ownerId;

    @Positive(message = "Id must be positive")
    private int parentProjectId;

    @NotNull(message = "Visibility must not be empty")
    private ProjectVisibility visibility;

    private Long coverImageId;
}
