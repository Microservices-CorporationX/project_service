package faang.school.projectservice.dto.stage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ActionWithTaskDto {
    private String action;
    private Long transferStageId;
}
