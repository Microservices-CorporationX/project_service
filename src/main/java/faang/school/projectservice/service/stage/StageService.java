package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.ActionWithTask;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageValidator stageValidator;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;

    public StageDto createStage(StageDto stageDto) {
        log.info("Start of the stage creation process. ID: {} Name: {}", stageDto.getStageId(), stageDto.getStageName());

        Stage stageEntity = stageMapper.toEntity(stageDto);
        save(stageEntity);

        log.info("The stage is saved in the database. ID: {} Name: {}", stageDto.getStageId(), stageDto.getStageName());

        log.info("Stage successfully created and saved. ID: {} Name: {}", stageDto.getStageId(), stageDto.getStageName());
        return stageMapper.toDto(stageEntity);
    }

    public List<StageDto> getStageByFilter(StageFilterDto stageFilterDto) {
        log.info("Starting to fetch stages with filter");

        stageValidator.validationOnNull(stageFilterDto, "Stage filter DTO cannot be null");
        log.debug("Stage filter DTO validation passed");

        log.debug("Retrieving all stages from repository");
        List<Stage> allStages = findAll();
        log.info("Retrieved {} stages from repository", allStages.size());

        Stream<Stage> stageStream = allStages.stream();

        log.debug("Applying filters to stages");
        List<StageDto> result = stageFilters.stream()
                .filter(filter -> {
                    boolean applicable = filter.isApplicable(stageFilterDto);
                    log.debug("Filter {} is {}", filter.getClass().getSimpleName(), applicable ? "applicable" : "not applicable");
                    return applicable;
                })
                .flatMap(filter -> {
                    log.debug("Applying filter: {}", filter.getClass().getSimpleName());
                    return filter.apply(stageStream, stageFilterDto);
                })
                .map(stage -> {
                    StageDto dto = stageMapper.toDto(stage);
                    log.trace("Mapped Stage {} to DTO", stage.getStageId());
                    return dto;
                })
                .toList();

        log.info("Found {} stages matching the filter", result.size());

        stageValidator.validationOnNull(result, "No stages found matching the filter");

        log.info("Successfully completed fetching stages by filter");
        return result;
    }

    public void deleteStage(Long stageId, ActionWithTask actionWithTask, Long transferStageId) {
        log.info("Starting stage deletion process. ID: {}, Action: {}, Transfer Stage ID: {}",
                stageId, actionWithTask, transferStageId);

        stageValidator.validationNumOnNullAndLessThanZero(stageId, "Stage ID cannot be null or less than zero");
        stageValidator.validationOnNull(actionWithTask, "Action with task cannot be null");

        log.debug("Retrieving stage with ID: {}", stageId);
        Stage stage = getById(stageId);
        log.debug("Stage successfully retrieved. Name: {}", stage.getStageName());

        switch (actionWithTask) {
            case CASCADE -> {
                log.info("Performing cascade deletion for stage: {}", stageId);
                performCascadeDelete(stage);
            }
            case CLOSE -> {
                log.info("Performing close action for stage: {}", stageId);
                performCloseAction(stage);
            }
            case TRANSFER -> {
                log.info("Performing transfer action for stage {} to stage: {}", stageId, transferStageId);
                stageValidator.validationNumOnNullAndLessThanZero(transferStageId,
                        "Transfer stage ID cannot be null or less than zero");
                performTransferAction(stage, transferStageId);
            }
            default -> {
                log.error("Unknown action with task: {}", actionWithTask);
                throw new DataValidationException("Unknown action with task: " + actionWithTask);
            }
        }

        log.info("Stage {} successfully deleted", stageId);
    }

    public StageDto updateStage(StageDto stageDto) {
        log.info("Starting update process for stage with ID: {}", stageDto.getStageId());

        stageValidator.validationOnNull(stageDto, "Stage DTO cannot be null");

        log.debug("Fetching stage with ID: {}", stageDto.getStageId());
        Stage stage = getById(stageDto.getStageId());
        log.debug("Stage fetched successfully. Current name: {}", stage.getStageName());

        log.debug("Fetching project team members");
        List<TeamMember> projectTeamMembers = stage.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();
        log.debug("Fetched {} project team members", projectTeamMembers.size());

        log.debug("Calculating current role count");
        Map<TeamRole, Long> currentRoleCount = stage.getExecutors().stream()
                .flatMap(member -> member.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));
        log.debug("Current role count: {}", currentRoleCount);

        log.info("Ensuring required roles for stage");
        ensureRequiredRoles(stage, currentRoleCount, projectTeamMembers);

        log.debug("Updating stage name from '{}' to '{}'", stage.getStageName(), stageDto.getStageName());
        stage.setStageName(stageDto.getStageName());

        log.debug("Saving updated stage");
        Stage updatedStage = save(stage);
        log.info("Stage successfully updated and saved. ID: {}, New name: {}", updatedStage.getStageId(), updatedStage.getStageName());

        StageDto updatedStageDto = stageMapper.toDto(updatedStage);
        log.debug("Mapped updated stage to DTO");

        return updatedStageDto;
    }


    public List<StageDto> getAllProjectStages(Long projectId) {
        log.info("Fetching all stages for project with ID: {}", projectId);

        stageValidator.validationOnNull(projectId, "Project ID cannot be null");
        stageValidator.validationNumOnNullAndLessThanZero(projectId, "Project ID must be a positive number");

        log.debug("Retrieving all stages from repository");
        List<Stage> stages = getProjectById(projectId).getStages();
        log.debug("Successfully fetched project");

        stageValidator.validationOnNullOrEmptyList(stages, "No stages found for project");

        log.info("Found {} stages for project {}", stages.size(), projectId);

        log.debug("Mapping stages to DTOs");
        return stages.stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(Long stageId) {
        log.info("Starting to fetch stage with ID: {}", stageId);

        stageValidator.validationOnNull(stageId, "Stage ID cannot be null");
        stageValidator.validationNumOnNullAndLessThanZero(stageId, "Stage ID must be a positive number");

        log.debug("Attempting to retrieve stage from repository");
        Stage stage = getById(stageId);

        log.info("Stage found. ID: {}, Name: {}", stage.getStageId(), stage.getStageName());

        log.debug("Validating stage data");
        stageValidator.validationOnNull(stage.getStageName(), "Stage name cannot be null");
        stageValidator.validationOnEmptyString(stage.getStageName(), "Stage name cannot be empty");

        log.debug("Mapping stage to DTO");
        StageDto stageDto = stageMapper.toDto(stage);

        log.info("Successfully fetched and mapped stage. ID: {}, Name: {}", stageDto.getStageId(), stageDto.getStageName());
        return stageDto;
    }

    private void performCascadeDelete(Stage stage) {
        log.info("Starting CASCADE delete for stage: {}", stage.getStageId());

        stageValidator.validationOnNull(stage, "Stage cannot be null");
        stageValidator.validationOnNull(stage.getStageId(), "Stage ID cannot be null");

        log.debug("Fetching tasks for stage: {}", stage.getStageId());
        List<Task> stageTasks = new ArrayList<>(stage.getTasks());
        log.info("Found {} tasks for stage: {}", stageTasks.size(), stage.getStageId());

        log.debug("Clearing tasks from stage: {}", stage.getStageId());
        stageTasks.clear();
        stage.setTasks(stageTasks);
        log.info("All tasks cleared from stage: {}", stage.getStageId());

        log.debug("Deleting stage from repository: {}", stage.getStageId());
        delete(stage);
        log.info("Stage successfully deleted: {}", stage.getStageId());

        log.info("CASCADE delete completed for stage: {}", stage.getStageId());
    }

    private void performCloseAction(Stage stage) {
        log.info("Starting CLOSE action for stage: {}", stage.getStageId());

        stageValidator.validationOnNull(stage, "Stage cannot be null");
        stageValidator.validationOnNull(stage.getStageId(), "Stage ID cannot be null");

        log.debug("Fetching tasks for stage: {}", stage.getStageId());
        List<Task> stageTasks = stage.getTasks();
        log.info("Found {} tasks for stage: {}", stageTasks.size(), stage.getStageId());

        log.debug("Updating status of tasks to CANCELLED for stage: {}", stage.getStageId());
        stageTasks.forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
            log.trace("Task {} status updated to CANCELLED", task.getId());
        });
        log.info("All tasks updated to CANCELLED status for stage: {}", stage.getStageId());

        log.debug("Deleting stage from repository: {}", stage.getStageId());
        delete(stage);
        log.info("Stage successfully deleted: {}", stage.getStageId());

        log.info("CLOSE action completed for stage: {}", stage.getStageId());
    }

    private void performTransferAction(Stage stage, Long transferStageId) {
        log.info("Starting TRANSFER action from stage: {} to stage: {}", stage.getStageId(), transferStageId);

        stageValidator.validationOnNull(stage, "Source stage cannot be null");
        stageValidator.validationOnNull(transferStageId, "Transfer stage ID cannot be null");
        stageValidator.validationNumOnNullAndLessThanZero(transferStageId, "Transfer stage ID must be a positive number");

        log.debug("Fetching transfer stage with ID: {}", transferStageId);
        Stage transferStage = getById(transferStageId);
        stageValidator.validationOnNull(transferStage, "Transfer stage not found for ID: " + transferStageId);
        log.debug("Transfer stage fetched successfully. Name: {}", transferStage.getStageName());

        stageValidator.validateDifferentStages(stage, transferStage, "Source and transfer stages cannot be the same");

        int taskCount = stage.getTasks().size();
        log.info("Preparing to transfer {} tasks from stage {} to stage {}", taskCount, stage.getStageId(), transferStageId);

        stageValidator.validationOnNull(stage.getTasks(), "Source stage tasks list cannot be null");

        log.debug("Starting task transfer process");
        stage.getTasks().forEach(task -> {
            stageValidator.validationOnNull(task, "Task cannot be null");
            stageValidator.validationOnNull(task.getId(), "Task ID cannot be null");

            log.trace("Transferring task {} from stage {} to stage {}", task.getId(), stage.getStageId(), transferStageId);
            task.setStage(transferStage);
            log.debug("Task {} successfully transferred to stage {}", task.getId(), transferStageId);
        });

        log.info("All tasks transferred. Saving updated transfer stage");
        save(transferStage);
        log.info("Successfully saved updated transfer stage {}", transferStageId);

        log.debug("Preparing to delete original stage {}", stage.getStageId());
        delete(stage);
        log.info("Successfully deleted original stage {}", stage.getStageId());

        log.info("Completed TRANSFER action. {} tasks moved from stage {} to stage {}", taskCount, stage.getStageId(), transferStageId);
    }

    private void ensureRequiredRoles(Stage stage, Map<TeamRole, Long> currentRoleCount, List<TeamMember> projectTeamMembers) {
        log.info("Starting to ensure required roles for stage: {}", stage.getStageId());

        stageValidator.validationOnNull(stage, "Stage cannot be null");
        stageValidator.validationOnNull(stage.getStageId(), "Stage ID cannot be null");
        stageValidator.validationOnNull(currentRoleCount, "Current role count map cannot be null");
        stageValidator.validationOnNull(projectTeamMembers, "Project team members list cannot be null");

        log.debug("Validating stage roles");
        stageValidator.validationOnNull(stage.getStageRoles(), "Stage roles cannot be null");
        stageValidator.validationOnNullOrEmptyList(stage.getStageRoles(), "Stage must have at least one role defined");

        log.info("Processing {} stage roles for stage: {}", stage.getStageRoles().size(), stage.getStageId());

        stage.getStageRoles().forEach(stageRole -> {
            log.debug("Processing stage role: {}", stageRole);

            stageValidator.validationOnNull(stageRole, "Stage role cannot be null");
            TeamRole requiredRole = stageRole.getTeamRole();
            stageValidator.validationOnNull(requiredRole, "Required team role cannot be null");

            int requiredCount = stageRole.getCount();
            stageValidator.validationNumOnNullAndLessThanZero(requiredCount, "Required count must be a positive number");

            long currentCount = currentRoleCount.getOrDefault(requiredRole, 0L);
            log.debug("Role: {}. Required: {}, Current: {}", requiredRole, requiredCount, currentCount);

            if (currentCount < requiredCount) {
                int invitationsNeeded = requiredCount - (int) currentCount;
                log.info("Need to invite {} members for role: {} in stage: {}", invitationsNeeded, requiredRole, stage.getStageId());
                inviteNewMembers(stage, requiredRole, invitationsNeeded, projectTeamMembers, currentRoleCount);
            } else {
                log.debug("Sufficient members for role: {} in stage: {}. Required: {}, Current: {}",
                        requiredRole, stage.getStageId(), requiredCount, currentCount);
            }
        });

        log.info("Finished ensuring required roles for stage: {}. Current role counts: {}", stage.getStageId(), currentRoleCount);
    }

    private void inviteNewMembers(Stage stage, TeamRole requiredRole,
                                  int invitationsNeeded,
                                  List<TeamMember> projectTeamMembers,
                                  Map<TeamRole, Long> currentRoleCount) {
        log.info("Starting inviteNewMembers process for stage: {}, role: {}, invitations needed: {}",
                stage.getStageId(), requiredRole, invitationsNeeded);

        stageValidator.validationOnNull(stage, "Stage cannot be null");
        stageValidator.validationOnNull(stage.getStageId(), "Stage ID cannot be null");
        stageValidator.validationOnNull(requiredRole, "Required role cannot be null");
        stageValidator.validationNumOnNullAndLessThanZero(invitationsNeeded, "Invitations needed must be a positive number");
        stageValidator.validationOnNullOrEmptyList(projectTeamMembers, "Project team members list cannot be null or empty");
        stageValidator.validationOnNull(currentRoleCount, "Current role count map cannot be null");

        log.debug("Validated input parameters for inviteNewMembers");

        log.info("Searching for {} candidates with role {} among {} project team members",
                invitationsNeeded, requiredRole, projectTeamMembers.size());

        List<TeamMember> candidates = findCandidates(projectTeamMembers, requiredRole, stage.getExecutors(), invitationsNeeded);

        log.debug("Found {} candidates for role: {} in stage: {}", candidates.size(), requiredRole, stage.getStageId());
        stageValidator.validationOnNullOrEmptyList(candidates, "No suitable candidates found for role " + requiredRole + " in stage " + stage.getStageId());

        log.info("Updating stage executors with {} new members", candidates.size());
        updateStageExecutors(stage, candidates);

        log.info("Sending invitations to {} candidates for stage: {}", candidates.size(), stage.getStageId());
        sendInvitations(stage, candidates);

        log.debug("Updating current role count for role: {}", requiredRole);
        updateCurrentRoleCount(currentRoleCount, requiredRole, candidates.size());

        log.info("Successfully invited {} new members for role: {} in stage: {}", candidates.size(), requiredRole, stage.getStageId());
        log.debug("Updated current role count: {}", currentRoleCount.get(requiredRole));

        if (candidates.size() < invitationsNeeded) {
            log.warn("Could not find enough candidates. Needed: {}, Found: {}", invitationsNeeded, candidates.size());
        }
    }

    private void updateStageExecutors(Stage stage, List<TeamMember> newMembers) {
        log.info("Starting to update stage executors for stage: {}", stage.getStageId());

        stageValidator.validationOnNull(stage, "Stage cannot be null");
        stageValidator.validationOnNull(stage.getStageId(), "Stage ID cannot be null");
        stageValidator.validationOnNullOrEmptyList(newMembers, "New members list cannot be null or empty");

        log.debug("Validating current executors list");
        stageValidator.validationOnNull(stage.getExecutors(), "Current executors list cannot be null");

        List<TeamMember> updatedExecutors = new ArrayList<>(stage.getExecutors());
        log.debug("Current number of executors: {}", updatedExecutors.size());

        log.info("Adding {} new members to the executors list", newMembers.size());
        updatedExecutors.addAll(newMembers);

        log.debug("Validating updated executors list");
        stageValidator.validationOnNullOrEmptyList(updatedExecutors, "Updated executors list cannot be null or empty");

        stage.setExecutors(updatedExecutors);
        log.info("Stage executors updated. New total number of executors: {}", updatedExecutors.size());
    }

    private void updateCurrentRoleCount(Map<TeamRole, Long> currentRoleCount, TeamRole role, int addedCount) {
        log.info("Updating current role count for role: {} with added count: {}", role, addedCount);

        stageValidator.validationOnNull(currentRoleCount, "Current role count map cannot be null");
        stageValidator.validationOnNull(role, "Role cannot be null");
        stageValidator.validationNumOnNullAndLessThanZero(addedCount, "Added count must be a positive number");

        Long previousCount = currentRoleCount.get(role);
        currentRoleCount.merge(role, (long) addedCount, Long::sum);
        Long newCount = currentRoleCount.get(role);

        log.debug("Role count updated. Role: {}, Previous count: {}, New count: {}", role, previousCount, newCount);
    }

    private List<TeamMember> findCandidates(List<TeamMember> projectTeamMembers, TeamRole requiredRole,
                                            List<TeamMember> currentExecutors, int invitationsNeeded) {
        log.debug("Finding candidates for role: {}. Invitations needed: {}", requiredRole, invitationsNeeded);

        stageValidator.validationOnNullOrEmptyList(projectTeamMembers, "Project team members cannot be null or empty");

        List<TeamMember> candidates = projectTeamMembers.stream()
                .filter(member -> member.getRoles().contains(requiredRole) && !currentExecutors.contains(member))
                .limit(invitationsNeeded)
                .collect(Collectors.toList());

        log.info("Found {} candidates for role: {}", candidates.size(), requiredRole);
        return candidates;
    }

    private void sendInvitations(Stage stage, List<TeamMember> candidates) {
        log.info("Sending invitations for stage: {} to {} candidates", stage.getStageId(), candidates.size());

        stageValidator.validationOnNullOrEmptyList(candidates, "No candidates found for invitation");

        candidates.forEach(candidate -> {
            StageInvitation invitation = StageInvitation.builder()
                    .stage(stage)
                    .invited(candidate)
                    .status(StageInvitationStatus.PENDING)
                    .build();
            save(invitation);
            log.debug("Sent invitation to member: {} for stage: {}", candidate.getId(), stage.getStageId());
        });

        log.info("Finished sending invitations for stage: {}", stage.getStageId());
    }

    private StageInvitation save(StageInvitation stageInvitation) {
        log.info("Saving stage invitation: {}", stageInvitation);

        stageValidator.validationOnNull(stageInvitation, "Stage invitation cannot be null");

        StageInvitation savedInvitation = stageInvitationRepository.save(stageInvitation);

        log.info("Successfully saved stage invitation with ID: {}", savedInvitation.getId());
        return savedInvitation;
    }

    private List<Stage> findAll() {
        log.info("Retrieving all stages");

        List<Stage> stages = stageRepository.findAll();

        log.info("Retrieved {} stages", stages.size());
        return stages;
    }

    private Stage save(Stage stage) {
        log.info("Saving stage: {}", stage);

        stageValidator.validationOnNull(stage, "Stage cannot be null");

        Stage savedStage = stageRepository.save(stage);

        log.info("Successfully saved stage with ID: {}", savedStage.getStageId());
        return savedStage;
    }

    private Stage getById(Long stageId) {
        log.info("Retrieving stage by ID: {}", stageId);

        stageValidator.validationOnNull(stageId, "Stage ID cannot be null");

        Stage stage = stageRepository.getById(stageId);

        stageValidator.validationOnNull(stage, "Stage not found for ID: " + stageId);

        log.info("Retrieved stage: {}", stage);
        return stage;
    }

    private Project getProjectById(Long projectId) {
        log.info("Retrieving project by ID: {}", projectId);

        stageValidator.validationOnNull(projectId, "Project ID cannot be null");

        Project project = projectRepository.getProjectById(projectId);

        stageValidator.validationOnNull(project, "Project not found for ID: " + projectId);

        log.info("Retrieved project: {}", project);
        return project;
    }

    private void delete(Stage stage) {
        log.info("Deleting stage: {}", stage);

        stageValidator.validationOnNull(stage, "Stage cannot be null");

        stageRepository.delete(stage);
        log.info("Successfully deleted stage with ID: {}", stage.getStageId());
    }
}
