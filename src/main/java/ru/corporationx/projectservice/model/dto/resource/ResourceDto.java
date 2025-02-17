package faang.school.projectservice.model.dto.resource;

import lombok.Builder;
import lombok.Data;
import ru.corporationx.projectservice.model.entity.ResourceStatus;
import ru.corporationx.projectservice.model.entity.ResourceType;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private ResourceType type;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Long project;
}
