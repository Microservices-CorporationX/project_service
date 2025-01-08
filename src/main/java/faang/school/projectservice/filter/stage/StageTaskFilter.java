package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageTaskFilter implements Filter<Stage, StageFilterDto> {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getTaskStatuses() != null &&
                !stageFilterDto.getTaskStatuses().isEmpty();
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getTasks().stream()
                .map(Task::getStatus)
                .anyMatch(status -> stageFilterDto.getTaskStatuses().contains(status))
        );

    }

}
