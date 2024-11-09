package faang.school.projectservice.dto.stage_invitation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<Long> executorsId;
}

