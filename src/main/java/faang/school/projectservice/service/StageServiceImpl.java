package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final StageRolesRepository stageRolesRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;

    @Override
    public StageDto createStage(StageDto stageDto) {
        return saveValidStage(stageDto);
    }


    @Override
    public StageDto updateStage(StageDto stageDto) {
        if (null == stageDto.getStageId()) {
            throw new DataValidationException("Stage id cannot be null");
        }
        return saveValidStage(stageDto);
    }

    @Override
    public List<StageDto> getAllStagesByFilter(StageFilterDto filter) {
        validateRole(filter);
        validateStatus(filter);
        return  getStagesByFilter(filter);
    }

    @Override
    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream().map(stageMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteStage(long stageId) {
        stageRepository.deleteById(stageId);
        taskRepository.updateStatus(stageId, TaskStatus.DONE.name());

    }

    @Override
    public StageDto getStageById(long stageId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new ResourceNotFoundException("Stage with id " + stageId + " not found"));
        return stageMapper.toDto(stage);
    }

    private void validateRole(StageFilterDto filter) {
        if (null != filter.getRole() && !filter.getRole().isEmpty() && !isValidateRole(filter.getRole())) {
            throw new DataValidationException("Invalid role" + filter.getRole());
        }
    }

    private void validateStatus(StageFilterDto filter) {
        if (null != filter.getStatus() && !filter.getStatus().isEmpty() && !isValidStatus(filter.getStatus())) {
            throw new DataValidationException("Invalid status" + filter.getStatus());
        }
    }

    private StageDto saveValidStage(StageDto stageDto) {
        Project project = validateProject(stageDto);
        if (projectRepository.existsByOwnerIdAndName(stageDto.getUserId(), project.getName())) {
            Stage stage = stageMapper.toEntity(stageDto);
            stage.setProject(project);
            List<StageRoles> stageRoles = stageDto.getStageRoles().stream().map(stageRolesMapper::toEntity).collect(Collectors.toList());
            stageRoles.forEach(stageRole -> stageRole.setStage(stage));
            stage.setStageRoles(stageRoles);
            Stage savedStage = stageRepository.save(stage);
            return stageMapper.toDto(savedStage);
        }
        throw new DataValidationException(String.format("Project with id %d for this user %d not found", stageDto.getProjectId(), stageDto.getUserId()));
    }

    public boolean isValidStatus(String status) {
        return Arrays.stream(TaskStatus.values()).anyMatch(enumValue -> enumValue.name().equals(status.toUpperCase()));
    }

    private boolean isValidateRole(String role) {
        return Arrays.stream(TeamRole.values()).anyMatch(enumValue -> enumValue.name().equals(role.toUpperCase()));
    }

    public List<StageRoles> getStageRolesByIds(StageDto stageDto) {
        return stageDto.getStageRoles().stream().map(stageRolesDto -> stageRolesDto.getId())
                .map(stageRolesId -> stageRolesRepository.findById(stageRolesId)
                        .orElseThrow(() -> new DataValidationException("StageRoles with id " + stageRolesId + " not found")))
                .toList();
    }

    private Project validateProject(StageDto stageDto) {
        Project project = projectRepository.findById(stageDto.getProjectId())
                .orElseThrow(() -> new DataValidationException("Project with id " + stageDto.getProjectId() + " not found"));
        if (List.of(ProjectStatus.COMPLETED, ProjectStatus.CANCELLED).contains(project.getStatus())) {
            throw new DataValidationException("Project with id " + stageDto.getProjectId() + " is in an invalid status: " + project.getStatus());
        }
        return project;
    }
    private List<StageDto> getStagesByFilter(StageFilterDto filter) {
        return stageRepository.findAll().stream()
                .filter(stage -> (null == filter.getRole() || filter.getRole().isEmpty()
                        || stage.getStageRoles().stream().anyMatch(role -> role.getTeamRole().name().equalsIgnoreCase(filter.getRole())))
                        && (null == filter.getStatus() || filter.getStatus().isEmpty()
                        || stage.getTasks().stream().anyMatch(task -> task.getStatus().name().equalsIgnoreCase(filter.getStatus()))))
                .map(stageMapper::toDto).toList();
    }
}
