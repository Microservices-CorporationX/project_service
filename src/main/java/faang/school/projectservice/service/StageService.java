package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.stage.DeleteStageRequest;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.UpdateStageRequest;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectServiceImpl projectService;
    private final TeamMemberRepository teamMemberRepository;


    public StageDto create(@Valid StageDto stageDto) {
        Project project = projectRepository.findById(stageDto.projectId())
                .orElseThrow(() -> new NotFoundException("Project with id"
                        + stageDto.projectId() + "not found"));

        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(project);
        return stageMapper.toDto(stageRepository.save(stage));
    }


    public List<StageDto> getStagesByProjectWithFilters(Long projectId, List<String> roles, String taskStatus) {
        Project project = projectService.getProject(projectId);

        List<Stage> stages = project.getStages();

        if (roles != null && !roles.isEmpty()) {
            stages = stages.stream()
                    .filter(stage -> stage.getStageRoles().stream()
                            .anyMatch(stageRole -> roles.contains(stageRole.getTeamRole().name())))
                    .collect(Collectors.toList());
        }

        if (taskStatus != null && !taskStatus.isBlank()) {
            try {
                TaskStatus statusToFind = TaskStatus.valueOf(taskStatus.toUpperCase());
                stages = stages.stream()
                        .filter(stage -> stage.getTasks().stream()
                                .anyMatch(task -> task.getStatus() == statusToFind))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return Collections.emptyList();
            }
        }
        return stageMapper.toDto(stages);
    }

    @Transactional
    public void deleteStageWithStrategy(DeleteStageRequest deleteStageRequest) {

        if (deleteStageRequest == null || deleteStageRequest.stageId() == null) {
            throw new IllegalArgumentException("StageDto and stageId cannot be null");
        }

        Stage stage = stageRepository.findById(deleteStageRequest.stageId())
                .orElseThrow(() -> new NotFoundException("Stage with id "
                        + deleteStageRequest.stageId() + " not found."));

        String deletionStrategy = deleteStageRequest.deletionStrategy();
        Long targetStageId = deleteStageRequest.targetStageId();

        if (deletionStrategy == null || deletionStrategy.isBlank()) {
            throw new IllegalArgumentException("Deletion strategy must not be null or blank");
        }

        switch (deletionStrategy.toUpperCase()) {
            case "CASCADE":
                taskRepository.deleteAllByStageId(stage.getStageId());
                stageRepository.delete(stage);
                break;

            case "CLOSE":
                List<Task> tasksToClose = taskRepository.findAllByStage_StageId(stage.getStageId());
                tasksToClose.forEach(Task::close);
                taskRepository.saveAll(tasksToClose);
                stageRepository.delete(stage);
                break;

            case "MOVE":
                if (targetStageId == null) {
                    throw new IllegalArgumentException(
                            "Target stage ID is required for MOVE strategy");
                }
                Stage targetStage = stageRepository.findById(targetStageId)
                        .orElseThrow(() -> new DataValidationException(
                                "Target stage with id " + targetStageId + " not found"));

                List<Task> tasksToMove = taskRepository.findAllByStage_StageId(stage.getStageId());
                tasksToMove.forEach(task -> task.setStage(targetStage));
                taskRepository.saveAll(tasksToMove);
                stageRepository.delete(stage);
                break;

            default:
                throw new IllegalArgumentException("Invalid deletion strategy: " + deletionStrategy);
        }
    }


    public StageDto update(UpdateStageRequest updateStageRequest) {
        Stage stage = stageRepository.findById(updateStageRequest.stageId())
                .orElseThrow(() -> new DataValidationException("Stage with id " + updateStageRequest.stageId() + " not found"));

        stage.setStageName(updateStageRequest.stageName());
        List<Long> executorIds = updateStageRequest.executorsIds();
        List<TeamMember> executors = teamMemberRepository.findAllById(executorIds);
        if (executors.size() != executorIds.size()) {
            throw new IllegalArgumentException("One or more executors not found");
        }

        stage.setExecutors(executors);

        List<String> requiredRoles = updateStageRequest.requiredRoles();
        List<TeamMember> projectMembers = teamMemberRepository.findByProjectId(updateStageRequest.projectId());
        for (String role : requiredRoles) {
            long currentCount = executors.stream()
                    .filter(executor -> executor.getRoles().contains(role))
                    .count();

            long requiredCount = Collections.frequency(requiredRoles, role);

            if (currentCount < requiredCount) {
                long missingCount = requiredCount - currentCount;

                List<TeamMember> availableMembers = projectMembers.stream()
                        .filter(member -> member.getRoles().contains(role) && !executors.contains(member))
                        .limit(missingCount)
                        .toList();

                if (availableMembers.size() < missingCount) {
                    throw new IllegalArgumentException(
                            "Not enough members with role " + role + " available in project");
                }
                executors.addAll(availableMembers);
                availableMembers.forEach(member -> sendInvitation(member, stage));
            }
        }
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toDto(updatedStage);
    }

    public List<StageDto> getAllStagesByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("Project with id " + projectId + " not found"));

        List<Stage> stages = project.getStages();
        return stageMapper.toDto(stages);
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DataValidationException("Stage with id " + stageId + " not found"));
        return stageMapper.toDto(stage);
    }

    // Заглушка
    private void sendInvitation(TeamMember member, Stage stage) {
        System.out.println("Метод-заглушка, приглашения реализуются в другой задаче");
    }
}