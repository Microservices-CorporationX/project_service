package ru.corporationx.projectservice.model.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<StageRolesDto> stageRolesDto;
}
