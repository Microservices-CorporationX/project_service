package faang.school.projectservice.update.tasks;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.update.TaskUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageUpdate implements TaskUpdate {
    private final StageRepository stageRepository;

    @Override
    public boolean isApplicable(TaskDto taskDto) {
        return taskDto.getStageId() != null;
    }

    @Override
    public void apply(Task task, TaskDto taskDto) {
        task.setStage(stageRepository.getById(taskDto.getStageId()));
    }
}

