package faang.school.projectservice.mapper.helper;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapperHelper {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;

    @Named("findProjectById")
    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Named("findTaskById")
    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElse(null);
    }

    @Named("findStageById")
    public Stage findStageById(Long stageId) {
        return stageRepository.findById(stageId)
                .orElse(null);
    }

    @Named("findTasksByIds")
    public List<Task> findTasksByIds(List<Long> taskIds) {
        return taskIds != null
                ? taskIds.stream()
                .map(this::findTaskById)
                .toList()
                : List.of();
    }
}
