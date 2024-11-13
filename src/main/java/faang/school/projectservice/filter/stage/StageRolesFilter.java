package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Controller;

import java.util.stream.Stream;

@Controller
public class StageRolesFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getStageRoles() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stage, StageFilterDto stageFilterDto) {
        return stage.filter(stage1 ->
                stage1.getStageRoles().stream()
                        .anyMatch(stageRole ->
                                stageRole.getTeamRole().equals(stageFilterDto.getStageRoles())
                        )
        );
    }
}
