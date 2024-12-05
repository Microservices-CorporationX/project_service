package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class ResourceDto {
    @Min(1)
    private Long id;
    @NotBlank(message = "Name should not be blank")
    private String name;
    @NotBlank(message = "Key should not be blank")
    private String key;
    @Min(0)
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Min(1)
    @NotNull(message = "createdById should not be null")
    private Long createdById;
    @Min(1)
    @NotNull(message = "updatedById should not be null")
    private Long updatedById;
    @Min(1)
    @NotNull(message = "projectId should not be null")
    private Long projectId;
}
