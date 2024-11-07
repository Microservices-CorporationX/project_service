package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceValidate {
    private final TeamRepository teamRepository;
    private final StageJpaRepository stageRepository;


    public List<Team> getTeams(CreateSubProjectDto dto) {
        if (dto.getTeamsIds() != null) {
            return teamRepository.findAllById(dto.getTeamsIds());
        }
        return null;
    }

    public List<Stage> getStages(CreateSubProjectDto dto) {
        if (dto.getStagesIds() != null) {
            return stageRepository.findAllById(dto.getStagesIds());
        } else return null;
    }

    public boolean isVisibilityDtoAndProjectNotEquals(CreateSubProjectDto dto, Project project) {
        return dto.getVisibility() != project.getVisibility();
    }

    public boolean isStatusDtoAndProjectNotEquals(CreateSubProjectDto dto, Project project) {
        return dto.getStatus() != project.getStatus();
    }


}
