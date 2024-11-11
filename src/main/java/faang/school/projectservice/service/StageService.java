package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final StageMapper stageMapper;
    private final StageValidator stageValidator;

    public void setExecutor(Long stageId, Long executorId) {
        Stage stage = stageRepository.getById(stageId);
        List<TeamMember> executors = stage.getExecutors();
        executors.add(teamMemberService.getTeamMemberByUserId(executorId));
        stage.setExecutors(executors);

        stageRepository.save(stage);
    }

    public Stage getById(Long stageId) {
        return stageRepository.getById(stageId);
    }

    public boolean existsById(Long stageId) {
        return stageRepository.existsById(stageId);
    }

    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        Project project = projectService.getProjectById(stageDto.getProjectId());
        stage.setProject(project);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public List<StageDto> getStagesBy(long projectId, String role, String status) {
        validateInput(projectId, role, status);
        return stageRepository.findAll().stream()
                .filter(stage -> stage.getProject().getId().equals(projectId))
                .filter(stage -> stage.getStageRoles().stream()
                        .map(stageRole -> stageRole.getTeamRole().toString())
                        .anyMatch(role.toLowerCase()::equals))
                .filter(stage -> stage.getTasks().stream()
                        .map(task -> task.getStatus().toString())
                        .anyMatch(status.toLowerCase()::equals))
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        stageRepository.delete(stage);
    }

    public void deleteStageAndMoveTasks(long stageId, long newStageId) {
        Stage stage = stageRepository.getById(stageId);
        Stage newStage = stageRepository.getById(newStageId);
        newStage.setTasks(stage.getTasks());
        stageRepository.delete(stage);
    }

    private void validateInput(long projectId, String role, String status) {
        if (projectService.existsById(projectId)) {
            stageValidator.validateStageRole(role);
            stageValidator.validateTaskStatus(status);
        } else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }
}
