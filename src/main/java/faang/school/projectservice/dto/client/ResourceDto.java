package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private Long createdById;
    private Long projectId;
}
