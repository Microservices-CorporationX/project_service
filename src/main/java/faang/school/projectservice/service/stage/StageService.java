package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.entity.stage.DeleteStrategy;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.*;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.notification.NotificationService;
import faang.school.projectservice.service.team.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.exception.ExceptionMessages.PROJECT_NOT_FOUND;
import static faang.school.projectservice.exception.ExceptionMessages.WRONG_PROJECT_STATUS;
import static faang.school.projectservice.exception.ExceptionMessages.PROJECT_STATUS_CANCELLED;


@Service
@Slf4j
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberService teamMemberService;
    private final StageMapper stageMapper;
    private final ProjectRepository projectRepository;
    private final List<StageFilter> stageFilters;

    public StageDto createStage(StageDto stageDto) {
        validateProject(stageDto.getProjectId());
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
            throw new DataValidationException(WRONG_PROJECT_STATUS.getMessage().formatted(stageDto.getProjectId()));
        }
        Stage newStage = stageMapper.toEntity(stageDto);
        newStage.getStageRoles().forEach(role -> role.setStage(newStage));
        Stage savedStage = stageRepository.save(newStage);
        log.info("Stage created: {}", savedStage.getStageId());
        return stageMapper.toDto(savedStage);
    }

    public List<StageDto> getFilteredStages(Long projectId, StageFilterDto filter) {
        Project project = projectRepository.getReferenceById(projectId);
        if (project.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException(PROJECT_STATUS_CANCELLED.getMessage());
        }
        Stream<Stage> stageStream = project.getStages().stream();
        log.info("Found {} filtered stages for project: {}", stageFilters.size(), projectId);
        return stageFilters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filter))
                .flatMap(stageFilter -> stageFilter.apply(stageStream, filter))
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(Long id, DeleteStrategy strategy, Long targetStageId) {
        Stage stage = stageRepository.getById(id);

        switch (strategy) {
            case CASCADE_DELETE:
                log.debug("Executing CASCADE_DELETE strategy");
                break;
            case CLOSE_TASKS:
                log.debug("Executing CLOSE_TASKS strategy");
                stage.getTasks().forEach(task -> task.setStatus(TaskStatus.DONE));
                break;
            case MOVE_TASKS:
                log.debug("Executing MOVE_TASKS strategy");
                if (targetStageId == null) {
                    log.error("Target stage ID is required for MOVE_TASKS strategy");
                    throw new IllegalArgumentException("Target stage ID is required for MOVE_TASKS strategy");
                }
                Stage targetStage = stageRepository.getById(targetStageId);
                stage.getTasks().forEach(task -> task.setStage(targetStage));
                break;
        }
        stageRepository.delete(stage);
        log.info("Stage removed: {}", id);
    }

    public StageDto updateStage(Long id, StageDto stageDto) {
        log.info("Updating stage with ID: {}", id);
        Stage existingStage = stageRepository.getById(id);
        existingStage.setStageName(stageDto.getStageName());
        List<StageRoles> newRoles = stageDto.getRoles().stream()
                .map(roleDto -> {
                    StageRoles role = stageMapper.toStageRoles(roleDto);
                    role.setStage(existingStage);
                    return role;
                })
                .toList();
        existingStage.getStageRoles().clear();
        existingStage.getStageRoles().addAll(newRoles);

        if (stageDto.getExecutorIds() != null) {
            log.debug("Updating executors for stage: {}", existingStage.getStageName());
            List<TeamMember> newExecutors = stageDto.getExecutorIds().stream()
                    .map(teamMemberService::getTeamMemberById)
                    .toList();
            existingStage.setExecutors(newExecutors);
        }

        validateRolesRequirements(existingStage);
        sendInvitationsIfNeeded(existingStage);

        Stage savedStage = stageRepository.save(existingStage);
        log.info("Stage '{}' updated successfully", savedStage.getStageName());
        return stageMapper.toDto(savedStage);
    }

    public StageDto getStage(Long id) {
        return stageMapper.toDto(stageRepository.getById(id));
    }

    private void sendInvitationsIfNeeded(Stage stage) {
        Map<TeamRole, Integer> requiredRoles = getRequiredRoles(stage);
        Map<List<TeamRole>, Long> actualRoles = getActualRoles(stage);

        for (Map.Entry<TeamRole, Integer> entry : requiredRoles.entrySet()) {
            TeamRole role = entry.getKey();
            int required = entry.getValue();
            long actual = actualRoles.getOrDefault(role, 0L);

            if (actual < required) {
                int needCount = required - (int) actual;
                List<TeamMember> availableMembers = findAvailableMembers(stage, role, needCount);

                if (!availableMembers.isEmpty()) {
                    stage.getExecutors().addAll(availableMembers);
                    sendInvitations(availableMembers, stage);
                } else {
                    log.warn("Not enough team members with role {} for stage {}. Required: {}, Current: {}",
                            role, stage.getStageName(), required, actual);
                }
            }
        }
    }

    private List<TeamMember> findAvailableMembers(Stage stage, TeamRole role, int needCount) {
        List<TeamMember> projectMembers = stage.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .toList();

        return projectMembers.stream()
                .filter(member -> member.getRoles().contains(role))
                .filter(member -> !stage.getExecutors().contains(member))
                .filter(member -> member.getStages().stream()
                        .noneMatch(memberStage ->
                                memberStage.getProject().equals(stage.getProject()) &&
                                        !memberStage.equals(stage)))
                .limit(needCount)
                .collect(Collectors.toList());
    }

    private void sendInvitations(List<TeamMember> members, Stage stage) {
        members.forEach(member -> NotificationService.sendInvitation(member, stage));
    }

    private void validateRolesRequirements(Stage stage) {
        Map<TeamRole, Integer> requiredRoles = getRequiredRoles(stage);
        Map<List<TeamRole>, Long> actualRoles = getActualRoles(stage);

        for (Map.Entry<TeamRole, Integer> entry : requiredRoles.entrySet()) {
            TeamRole role = entry.getKey();
            int required = entry.getValue();
            long actual = actualRoles.getOrDefault(Collections.singletonList(role), 0L);

            if (actual < required) {
                throw new DataValidationException(
                        String.format("Not enough executors with role %s: required %d, actual %d", role, required, actual));
            }
        }
    }
    private Map<TeamRole, Integer> getRequiredRoles(Stage stage) {
        return stage.getStageRoles().stream()
                .collect(Collectors.toMap(
                        StageRoles::getTeamRole,
                        StageRoles::getCount
                ));
    }

    private Map<List<TeamRole>, Long> getActualRoles(Stage stage) {
        return stage.getExecutors().stream()
                .collect(Collectors.groupingBy(
                        TeamMember::getRoles,
                        Collectors.counting()
                ));
    }

    private void validateProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException(PROJECT_NOT_FOUND.getMessage().formatted(projectId));
        }
    }
}