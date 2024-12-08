package faang.school.projectservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoverProjectDto {
    private String URI;
    private Long size;
    private Long projectId;
    private boolean deleted = false;

}
