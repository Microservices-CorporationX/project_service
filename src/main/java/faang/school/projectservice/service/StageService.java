package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.CreateStageRequest;
import faang.school.projectservice.dto.stage.DeleteStageRequest;
import faang.school.projectservice.dto.stage.StageResponse;
import faang.school.projectservice.dto.stage.UpdateStageRequest;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.enums.StrategyDelete;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProjectService projectService;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationService stageInvitationService;

    @Transactional
    public StageResponse create(@Valid CreateStageRequest createStageRequest) {
        Project project = findProjectById(createStageRequest.projectId());
        Stage stage = stageMapper.toEntity(createStageRequest);
        stage.setProject(project);
        return stageMapper.toResponse(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public List<StageResponse> getStagesByProjectWithFilters(Long projectId, List<String> roles, String taskStatus) {
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
        return stageMapper.toResponse(stages);
    }

    @Transactional
    public void deleteStageWithStrategy(DeleteStageRequest deleteStageRequest) {
        if (deleteStageRequest == null || deleteStageRequest.stageId() == null) {
            throw new IllegalArgumentException("DeleteStageRequest and stageId cannot be null");
        }

        Stage stage = findStageById(deleteStageRequest.stageId());

        if (deleteStageRequest.deletionStrategy() == null || deleteStageRequest.deletionStrategy().isBlank()) {
            throw new IllegalArgumentException("Deletion strategy must not be null or blank");
        }

        try {
            StrategyDelete strategy = StrategyDelete.valueOf(deleteStageRequest.deletionStrategy().toUpperCase());
            strategy.execute(stage, deleteStageRequest.targetStageId(), stageRepository, taskRepository);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid deletion strategy: " + deleteStageRequest.deletionStrategy());
        }
    }

    @Transactional
    public StageResponse update(UpdateStageRequest updateStageRequest) {
        stageMapper.validateUpdateStageRequest(updateStageRequest);
        Stage stage = findStageById(updateStageRequest.stageId());
        stageMapper.updateFromRequest(updateStageRequest, stage);

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
                    throw new IllegalArgumentException("Not enough members with role "
                            + role + " available in project");
                }
                executors.addAll(availableMembers);

                availableMembers.forEach(member -> {
                    StageInvitationDto stageInvitationDto = new StageInvitationDto(
                            null,
                            "Invitation to join stage: " + stage.getStageName(),
                            StageInvitationStatus.PENDING,
                            stage.getStageId(),
                            updateStageRequest.authorId(),
                            member.getId()
                    );
                    stageInvitationService.sendStageInvitation(stageInvitationDto);
                });
            }
        }
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toResponse(updatedStage);
    }

    @Transactional(readOnly = true)
    public List<StageResponse> getAllStagesByProject(Long projectId) {
        Project project = findProjectById(projectId);
        List<Stage> stages = project.getStages();
        return stageMapper.toResponse(stages);
    }

    @Transactional(readOnly = true)
    public StageResponse getStageById(Long stageId) {
        Stage stage = findStageById(stageId);
        return stageMapper.toResponse(stage);
    }

    private Stage findStageById(Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new DataValidationException("Stage with id "
                        + stageId + " not found"));
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project with id "
                        + projectId + " not found"));

    }
}


