package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectService projectService;
    private final StageMapper stageMapper;
    private final StageInvitationServiceImpl stageInvitationService;
    private final Random random = new Random();

    public StageDto create(StageDto stageDto) {
        Long projectId = stageDto.getProjectId();
        validate(projectId);
        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(projectService.getById(projectId));
        stage = stageRepository.save(stage);
        return stageMapper.toDto(stage);
    }

    public List<StageDto> getByRole(StageDto stageDto, TeamRole teamRole) {
        Long projectId = stageDto.getProjectId();
        validate(projectId);
        List<Stage> stages = projectService.getById(projectId).getStages();
        List<Stage> filteredStages = stages.stream()
                .filter(stage -> stage.getStageRoles().stream()
                        .anyMatch(role -> role.getTeamRole().equals(teamRole)))
                .toList();
        return stageMapper.toDto(filteredStages);
    }

    public List<StageDto> getByStatus(StageDto stageDto, TaskStatus status) {
        Long projectId = stageDto.getProjectId();
        validateExisting(projectId);
        List<Stage> stages = projectService.getById(projectId).getStages();
        List<Stage> filteredStages = stages.stream()
                .filter(stage -> stage.getTasks().stream()
                        .anyMatch(task -> task.getStatus().equals(status)))
                .toList();
        return stageMapper.toDto(filteredStages);
    }

    public StageDto deleteCascade(StageDto stageDto){
        validateExisting(stageDto.getProjectId());
        Stage stage = stageRepository.getById(stageDto.getStageId());
        List<Long> IdsTasksToDelete = stage.getTasks().stream().map(Task::getId).toList();
        Project project = projectService.getById(stageDto.getProjectId());

        List<Task> projectTasks = project.getTasks()
                .stream().filter(t -> !IdsTasksToDelete.contains(t.getId())).toList();
        project.setTasks(projectTasks);

        List<Stage> projectStages = project.getStages().stream()
                .filter(s -> !s.getStageId().equals(stageDto.getStageId())).toList();
        project.setStages(projectStages);

        projectService.save(project);
        return stageDto;
    }

    public StageDto postponeTasks(StageDto stageDto, Long nextStageId){
        Stage stage = stageRepository.getById(stageDto.getStageId());
        Stage postponeStage = stageRepository.getById(nextStageId);
        List<Task> tasksToPostpone = stage.getTasks();

        deleteCascade(stageDto);
        List<Task> updatedTasks = new ArrayList<>(tasksToPostpone);
        updatedTasks.addAll(postponeStage.getTasks());
        postponeStage.setTasks(updatedTasks);

        return stageMapper.toDto(stageRepository.save(postponeStage));
    }

    public StageDto update(StageDto stageDto) {
        validate(stageDto.getProjectId());
        Stage stage = stageMapper.toEntity(stageDto);
        Stage stageOrig = stageRepository.getById(stageDto.getStageId());
        List<StageRolesDto> dtoRoles = stageMapper.toDto(stage).getStageRolesDto();
        List<StageRolesDto> dtoRolesOrig = stageMapper.toDto(stageOrig).getStageRolesDto();

        sendMissingRolesInvite(dtoRoles, dtoRolesOrig, stageDto);

        stageMapper.update(stageDto, stageOrig);
        return stageMapper.toDto(stageRepository.save(stageOrig));
    }

    public List<StageDto> getAllStages(StageDto stageDto){
        validateExisting(stageDto.getProjectId());
        Project project = projectService.getById(stageDto.getProjectId());
        List<Stage> stages = project.getStages();
        return stageMapper.toDto(stages);
    }

    public StageDto getById(Long stageId){
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toDto(stage);
    }

    public void sendMissingRolesInvite(List<StageRolesDto> dtoRoles, List<StageRolesDto> dtoRolesOrig,
                                       StageDto stageDto) {
        Map<TeamRole, Integer> neededRolesMap = getNeededRolesMap(dtoRoles, dtoRolesOrig);

        if (!neededRolesMap.isEmpty()) {
            Project project = projectService.getById(stageDto.getProjectId());
            List<Team> teams = project.getTeams();

            for (Map.Entry<TeamRole, Integer> entry : neededRolesMap.entrySet()) {
                Set<Long> membersIdsSet = teams.stream()
                        .flatMap(team -> team.getTeamMembers().stream()
                                .filter(m -> m.getRoles().contains(entry.getKey()))
                                .map(TeamMember::getId)).collect(Collectors.toSet());
                List<Long> membersIds = membersIdsSet.stream().toList();

                for (int i = 0; i < entry.getValue(); i++) {
                    Long invitedId = membersIds.get(random.nextInt(0, membersIds.size()));
                    StageInvitationDto invitationDto = StageInvitationDto.builder()
                            .stageId(stageDto.getStageId())
                            .authorId(project.getOwnerId())
                            .invitedId(invitedId).build();
                    stageInvitationService.sendStageInvitation(invitationDto);
                }
            }
        }
    }

    public Map<TeamRole, Integer> getNeededRolesMap(List<StageRolesDto> dtoRoles, List<StageRolesDto> dtoRolesOrig) {
        Map<TeamRole, Integer> rolesMap = dtoRoles.stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole, StageRolesDto::getCount));
        Map<TeamRole, Integer> mapRolesOrig = dtoRolesOrig.stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole, StageRolesDto::getCount));

        Map<TeamRole, Integer> neededRolesMap = new HashMap<>();

        for (Map.Entry<TeamRole, Integer> entry : rolesMap.entrySet()) {
            if (mapRolesOrig.containsKey(entry.getKey())) {
                if (mapRolesOrig.get(entry.getKey()) < entry.getValue()) {
                    int rolesCount = entry.getValue() - mapRolesOrig.get(entry.getKey());
                    neededRolesMap.put(entry.getKey(), rolesCount);
                }
            } else {
                neededRolesMap.put(entry.getKey(), entry.getValue());
            }
        }
        return neededRolesMap;
    }

    public void validate(Long projectId) {
        validateExisting(projectId);
        ProjectStatus status = projectService.getById(projectId).getStatus();
        if (status == ProjectStatus.COMPLETED || status == ProjectStatus.CANCELLED) {
            log.error("Закрытый проект");
            throw new DataValidationException("Проект закрыт");
        }
    }

    public void validateExisting(Long projectId) {
        if (!projectService.existsById(projectId)){
            log.error("Не существующий проект");
            throw new DataValidationException("Проект не существует");
        }
    }
}
