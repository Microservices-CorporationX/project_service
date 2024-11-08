package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.InvalidStageTransferException;
import faang.school.projectservice.exception.NonExistentDeletionTypeException;
import faang.school.projectservice.exception.ProjectStatusValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.DeletionType;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;

    public StageDto createStage(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new ProjectStatusValidationException("The project status is " + project.getStatus());
        }
        Stage stageToSave = stageMapper.toEntity(stageDto);
        stageToSave.setProject(project);
        Stage savedStage = stageRepository.save(stageToSave);
        log.info("Stage was created with ID: " + savedStage.getStageId());
        return stageMapper.toDto(savedStage);
    }

    public List<StageDto> getFilteredProjectStages(Long projectId, TeamRole teamRole, TaskStatus taskStatus) {
        Project project = projectRepository.getProjectById(projectId);
        log.info("Fetched stages filtered by team role {} and task status {} for the project with ID: {}", teamRole, taskStatus, projectId);
        return project.getStages().stream()
                .filter(stage -> teamRole == null || stage.getStageRoles().stream()
                        .anyMatch(stageRoles -> stageRoles.getTeamRole().equals(teamRole)))
                .filter(stage -> taskStatus == null || stage.getTasks().stream()
                        .anyMatch(task -> task.getStatus().equals(taskStatus)))
                .map(stageMapper::toDto)
                .toList();
    }

    public void delete(Long stageId, DeletionType deletionType, Long targetStageId) {
        Stage stage = stageRepository.getById(stageId);
        switch (deletionType) {
            case DELETE -> {
                taskRepository.deleteAll(stage.getTasks());
                log.info("Tasks have been deleted for stage with ID: {}", stageId);
            }
            case CLOSE -> {
                stage.getTasks().forEach(task -> {
                    task.setStatus(TaskStatus.CANCELLED);
                    task.setUpdatedAt(LocalDateTime.now());
                    taskRepository.save(task);
                });
                log.info("Tasks have been cancelled for stage with ID: {}", stageId);
            }
            case TRANSFER -> {
                if (targetStageId == null) {
                    throw new InvalidStageTransferException("Target stage ID is required for task transfer");
                }
                Stage targetStage = stageRepository.getById(stageId);
                stage.getTasks().forEach(task -> {
                    task.setStage(targetStage);
                    task.setUpdatedAt(LocalDateTime.now());
                    taskRepository.save(task);
                });
                log.info("Tasks have been transferred from stage with ID: {} to stage with ID: {}", stageId, targetStageId);
            }
            default -> throw new NonExistentDeletionTypeException("Unknown deletion type: " + deletionType);
        }
        stageRepository.deleteById(stageId);
        log.info("The stage with ID {} has been deleted", stageId);
    }

    public StageDto update(Long stageId, StageDto stageDto) {
        Stage stage = stageRepository.getById(stageId);
        if (stageDto.getStageName() != null && !stageDto.getStageName().isBlank()) {
            stage.setStageName(stageDto.getStageName());
        }
        if (stageDto.getStageRoles() != null && !stageDto.getStageRoles().isEmpty()) {
            stage.setStageRoles(stageRolesMapper.toEntity(stageDto.getStageRoles()));
        }
        Stage updatedStage = stageRepository.save(stage);
        log.info("Stage with ID: {} has been updated", stageId);
        return stageMapper.toDto(updatedStage);
    }

    public List<StageDto> getAllStagesByProjectId(Long projectId) {
        log.info("Fetched all stages of the project with ID: {}", projectId);
        return stageMapper.toDto(stageRepository.findAllStagesByProjectId(projectId));
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        log.info("Fetched the stage with ID: {}", stageId);
        return stageMapper.toDto(stage);
    }
}
