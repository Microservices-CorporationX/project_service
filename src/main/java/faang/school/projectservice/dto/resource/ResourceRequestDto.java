package faang.school.projectservice.dto.resource;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceRequestDto {
    @Positive(message = "Project id must be positive")
    private long projectId;
}
