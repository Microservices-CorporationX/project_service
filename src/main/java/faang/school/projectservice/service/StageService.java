package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
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
    private final ProjectMapper projectMapper;

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
        Project project = projectMapper.toEntity(projectService.getById(stageDto.getProjectId()));
        stage.setProject(project);
        return stageMapper.toDto(stageRepository.save(stage));
    }
}
