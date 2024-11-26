package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageRolesFilter implements Filter<Stage, StageFilterDto> {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return (stageFilterDto.getTeamRoles() != null) && (!stageFilterDto.getTeamRoles().isEmpty());
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getStageRoles().stream()
                .map(StageRoles::getTeamRole)
                .anyMatch(teamRole -> stageFilterDto.getTeamRoles().contains(teamRole))
        );

    }
}
