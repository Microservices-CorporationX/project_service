package faang.school.projectservice.service;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final StageMapper stageMapper;
    private final List<Filter<Stage, StageFilterDto>> stageFilters;

    public void setExecutor(Long stageId, Long executorId) {
        Stage stage = stageRepository.getById(stageId);
        List<TeamMember> executors = stage.getExecutors();
        executors.add(teamMemberService.getTeamMemberByUserId(executorId));
        stage.setExecutors(executors);

        stageRepository.save(stage);
    }

    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        Project project = projectService.getProjectById(stageDto.getProjectId());
        stage.setProject(project);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public void updateStage(long stageId, TeamMemberDto teamMemberDto) {
    }

    public List<StageDto> getStagesByProjectIdFiltered(long projectId, StageFilterDto filters) {
        Stream<Stage> stage = stageRepository.findAllByProjectId(projectId).stream();
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(stage, (currentStream, filter) ->
                                filter.apply(currentStream, filters),
                        (s1, s2) -> s1)
                .map(stageMapper::toDto)
                .toList();
    }

    public List<StageDto> getStagesByProjectId(long projectId) {
        return stageRepository.findAllByProjectId(projectId).stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        stageRepository.delete(stage);
    }

    public void deleteStageAndMoveTasks(long stageId, long anotherStageId) {
        Stage stage = stageRepository.getById(stageId);
        Stage newStage = stageRepository.getById(anotherStageId);
        newStage.setTasks(stage.getTasks());
        stageRepository.delete(stage);
    }

    public StageDto getStage(long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    public boolean existsById(Long stageId) {
        return stageRepository.existsById(stageId);
    }

    public Stage getById(Long stageId) {
        return stageRepository.getById(stageId);
    }

    private boolean isParticipantWithRoleExist(Stage stage, String role) {
        return stage.getExecutors().stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole.toString().equalsIgnoreCase(role));
    }

    private int countOfStageUsersWithRole(Stage stage, String role) {
        return stage.getStageRoles().stream()
                .filter(stageRoles -> stageRoles.getTeamRole().toString().equalsIgnoreCase(role))
                .mapToInt(StageRoles::getCount)
                .findFirst()
                .orElse(0);
    }
}

