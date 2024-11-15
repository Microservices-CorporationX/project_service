package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectService projectService;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;

    public StageDto create(StageDto stageDto) {
        validate(stageDto.getProjectId());
        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(projectService.getById(stageDto.getProjectId()));
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
        validate(projectId);
        List<Stage> stages = projectService.getById(projectId).getStages();
        List<Stage> filteredStages = stages.stream()
                .filter(stage -> stage.getTasks().stream()
                        .anyMatch(task -> task.getStatus().equals(status)))
                .toList();
        return stageMapper.toDto(filteredStages);
    }

    public StageDto deleteCascade(StageDto stageDto){
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
        Stage stage = stageMapper.toEntity(stageDto);
        Stage stageOrig = stageRepository.getById(stageDto.getStageId());
        List<StageRolesDto> dtoRoles = stageRolesMapper.toDto(stage.getStageRoles());
        List<StageRolesDto> dtoRolesOrig = stageRolesMapper.toDto(stageOrig.getStageRoles());

        Map<TeamRole, Integer> mapRoles = dtoRoles.stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole, StageRolesDto::getCount));
        Map<TeamRole, Integer> mapRolesOrig = dtoRolesOrig.stream()
                .collect(Collectors.toMap(StageRolesDto::getTeamRole, StageRolesDto::getCount));

        Map<TeamRole, Integer> neededRolesMap = new HashMap<>();

        for (Map.Entry entry : mapRoles.entrySet()) {
                if (mapRolesOrig.containsKey(entry.getKey())) {
                    if (mapRolesOrig.get(entry.getKey()) < (Integer) entry.getValue()) {
                        int rolesCount = (Integer) entry.getValue() - mapRolesOrig.get(entry.getKey());
                        neededRolesMap.put((TeamRole) entry.getKey(), rolesCount);
                    }
                } else {
                    neededRolesMap.put((TeamRole) entry.getKey(), (Integer) entry.getValue());
                }
        }



        stageMapper.update(stageDto, stageOrig);
    }

    public List<StageDto> getAllStages(StageDto stageDto){
        Project project = projectService.getById(stageDto.getProjectId());
        List<Stage> stages = project.getStages();
        return stageMapper.toDto(stages);
    }

    public StageDto getById(Long stageId){
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toDto(stage);
    }

    private void validate(Long projectId) {
        if (!projectService.existsById(projectId)){
            log.error("Не существующий проект");
            throw new DataValidationException("Проект не существует");
        }
        ProjectStatus status = projectService.getById(projectId).getStatus();
        if (status == ProjectStatus.COMPLETED || status == ProjectStatus.CANCELLED) {
            log.error("Закрытый проект");
            throw new DataValidationException("Проект закрыт");
        }
    }
}
