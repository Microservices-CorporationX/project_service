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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableTransactionManagement
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationService stageInvitationService;

    public StageResponse create(@Valid CreateStageRequest createStageRequest) {
        Project project = projectRepository.findById(createStageRequest.projectId())
                .orElseThrow(() -> new NotFoundException("Project with id "
                        + createStageRequest.projectId() + " not found"));

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

        Stage stage = stageRepository.findById(deleteStageRequest.stageId())
                .orElseThrow(() -> new NotFoundException("Stage with id "
                        + deleteStageRequest.stageId() + " not found."));

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

    public StageResponse update(UpdateStageRequest updateStageRequest) {
        Stage stage = stageRepository.findById(updateStageRequest.stageId())
                .orElseThrow(() -> new DataValidationException("Stage with id "
                        + updateStageRequest.stageId() + " not found"));

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
                            null, // id generated automatically
                            "Invitation to join stage: " + stage.getStageName(), // Description
                            StageInvitationStatus.PENDING, // Status
                            stage.getStageId(), // Stage ID
                            updateStageRequest.authorId(), // Author ID
                            member.getId() // Invitee ID
                    );
                    stageInvitationService.sendStageInvitation(stageInvitationDto);
                });
            }
        }
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toResponse(updatedStage);
    }

    public List<StageResponse> getAllStagesByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("Project with id " + projectId + " not found"));

        List<Stage> stages = project.getStages();
        return stageMapper.toResponse(stages);
    }

    public StageResponse getStageById(Long stageId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DataValidationException("Stage with id " + stageId + " not found"));
        return stageMapper.toResponse(stage);
    }
}
