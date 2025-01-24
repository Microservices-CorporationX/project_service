package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String name;
    private String description;
    private LocalDateTime data;
    @NotNull
    private List<Long> projects;
    private List<Long> userIds;
    private String imageId;
}
