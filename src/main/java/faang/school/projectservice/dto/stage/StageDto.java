package faang.school.projectservice.dto.stage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<Long> stageRolesId;
    private List<Long> executorsId;
}
