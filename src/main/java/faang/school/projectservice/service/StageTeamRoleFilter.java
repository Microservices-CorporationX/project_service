package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageTeamRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getRoles() != null && !filters.getRoles().isEmpty();
//        return (filters.getStageName() != null) || (filters.getRoles() != null && !filters.getRoles().isEmpty());
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        Stream<Stage> filteredStages = stages;

//        if (filters.getStageName() != null) {
//            filteredStages = filteredStages.filter(stage -> stage.getStageName().contains(filters.getStageName()));
//        }

        if (filters.getRoles() != null && !filters.getRoles().isEmpty()) {
            filteredStages = filteredStages.filter(stage ->
                    stage.getStageRoles().stream()
                            .map(StageRoles::getTeamRole)
                            .anyMatch(filters.getRoles()::contains)
            );
        }

        return filteredStages;
    }
}
