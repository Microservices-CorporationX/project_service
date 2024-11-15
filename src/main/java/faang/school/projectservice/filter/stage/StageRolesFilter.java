package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Component
public class StageRolesFilter implements StageFilter {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getStageRolesDto() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> {
            AtomicBoolean isApply = new AtomicBoolean(false);
            stage.getStageRoles().forEach(stageRoles -> {
                List<StageRolesDto> stageRolesList = stageFilterDto.getStageRolesDto().stream().filter(stageRolesDto ->
                        stageRoles.getId().equals(stageRolesDto.getStageRolesId())).toList();
                isApply.set(stageRolesList.size() == stageFilterDto.getStageRolesDto().size());
            });
            return isApply.get();
        });
    }
}
