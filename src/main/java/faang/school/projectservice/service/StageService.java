package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.dto.StageRoleDto;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;

    private final StageRolesRepository stageRolesRepository;
    private final TaskRepository taskRepository;
    private final List<StageFilter> stageFilters;

    public StageDto createStage(StageDto stageDto) {

        validateStage(stageDto);

        Stage stage = new Stage();
        stage.setStageName(stageDto.getStageName());
        stage.setProject(projectRepository.getProjectById(stageDto.getProjectId()));
        Stage savedStage = stageRepository.save(stage);

        List<StageRoles> savedRoles = new ArrayList<>();
        for (StageRoleDto roleDto : stageDto.getStageRoles()) {
            if (roleDto.getCount() <= 0) {
                throw new IllegalArgumentException("Role count must be greater than zero.");
            }

            StageRoles stageRole = stageMapper.toRoleEntity(roleDto);
            stageRole.setStage(savedStage);
            savedRoles.add(stageRolesRepository.save(stageRole));
        }

        List<Task> savedTasks = new ArrayList<>();
        if (stageDto.getTasks() != null) {
            for (TaskDto taskDto : stageDto.getTasks()) {
                Task task = stageMapper.toTaskEntity(taskDto);
                task.setStage(savedStage);
                savedTasks.add(taskRepository.save(task));
            }
        }

        StageDto resultDto = stageMapper.toDto(savedStage);
        resultDto.setStageRoles(stageMapper.toRoleDtos(savedRoles));
        resultDto.setTasks(stageMapper.toTaskDtos(savedTasks));

        return resultDto;
    }

    public List<StageDto> getStagesByRolesAndTaskStatuses(StageFilterDto filters) {

        Stream<Stage> stages = stageRepository.findAll().stream();
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(stages, filters))
                .map(stageMapper::toDto)
                .toList();
    }

    public List<StageDto> getAllStages() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toDto(stage);
    }

    public void deleteStage(Long stageId, String taskAction, Long targetStageId) {
        Stage stage = stageRepository.getById(stageId);

        switch (taskAction) {
            case "cascade":
                log.info("Received request to delete tasks: {}", stage.getTasks());
                taskRepository.deleteAll(stage.getTasks());
                break;
            case "close":
                stage.getTasks().forEach(task -> {
                    task.setStatus(TaskStatus.DONE);
                    taskRepository.save(task);
                });
                break;
            case "move":
                if (targetStageId == null) {
                    throw new IllegalArgumentException("Target stage ID must be provided for moving tasks.");
                }
                Stage targetStage = stageRepository.getById(targetStageId);
                stage.getTasks().forEach(task -> {
                    task.setStage(targetStage);
                    taskRepository.save(task);
                });
                break;
            default:
                throw new IllegalArgumentException("Invalid task action specified.");
        }

        stageRepository.delete(stage);
    }

    private void validateStage(StageDto stageDto) {

        Long projectId = stageDto.getProjectId();
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project with ID " + projectId + " does not exist.");
        }

        if (stageDto.getStageRoles() == null || stageDto.getStageRoles().isEmpty()) {
            throw new IllegalArgumentException("Stage must have at least one role defined.");
        }

    }
}

