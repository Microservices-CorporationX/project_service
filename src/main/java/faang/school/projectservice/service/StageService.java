package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageValidator stageValidator;
    private final StageMapper stageMapper;
    private final ProjectService projectService;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRolesRepository stageRolesRepository;

    public StageDto createStage(StageDto stageDto) {

        stageValidator.validateStageCreation(stageDto);
        Stage stage = stageMapper.toStage(stageDto);
        stageRepository.save(stage);
        return stageMapper.toStageDto(stage);
    }

    public List<StageDto> getStages(Long projectId) {
        Project project = stageValidator.getValidProject(projectId);
        return project.getStages().stream()
                .map(stageMapper::toStageDto).toList();
    }

    public void deleteStage(Long stageId) {

        stageValidator.checkStageToRemove(stageId);

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Этап не найден"));
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            List<Long> listTasks = stage.getTasks().stream()
                    .map(Task::getId)
                    .toList();
            executor.submit(() -> taskRepository.deleteAllById(listTasks));
            List<Long> rollesr = stage.getStageRoles().stream()
                    .map(StageRoles::getId)
                    .toList();
            executor.submit(() -> stageRolesRepository.deleteAllById(rollesr));
            List<Long> exc = stage.getExecutors().stream()
                    .map(TeamMember::getId)
                    .toList();
            executor.submit(() -> teamMemberRepository.deleteAllById(exc));
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                throw new RuntimeException("Время ожидания завершения задач истекло");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new RuntimeException("Удаление было прервано", e);
        } finally {
            stageRepository.delete(stage);
        }
    }

    public StageUpdateDto updateStage(Long stageId, StageUpdateDto stageUpdateDto) {

        StageDto stageDto = stageMapper.toStageDto(stageUpdateDto);
        stageValidator.checkStageForUpdate(stageId, stageUpdateDto);
        stageRepository.save(stageMapper.toStage(stageDto));
        return stageMapper.toStageUpdateDto(stageDto);
    }

    public ResponseEntity<StageDto> sendInvitations(Long stageId, Stage stage) {
        return null;
    }

    public ResponseEntity<StageDto> getStageDetails(Long stageId) {

        return null;
    }

    public ResponseEntity<List<Task>> getStageTasks(Long stageId, TaskStatus status) {
        return null;
    }

    public ResponseEntity<StageUpdateDto> updateStageParticipants(Set<StageUpdateDto> stageUpdateDto) {
        return null;
    }
}
