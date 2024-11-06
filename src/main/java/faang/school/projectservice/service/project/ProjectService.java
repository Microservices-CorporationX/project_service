package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final StageJpaRepository stageRepository;
    private final TeamRepository teamRepository;

    public CreateSubProjectDto createSubProject(Long parentId, CreateSubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(parentId);
        Project childProject = subProjectMapper.toEntity(subProjectDto);
        validateVisibilityProjectAndSubProject(parentProject, childProject);

        log.info("Got child project from Dto withing creating subproject with id = {}", childProject.getId());
        childProject.setParentProject(parentProject);
        parentProject.getChildren().add(childProject);
        parentProject.setStages(getStages(subProjectDto));
        parentProject.setTeams(getTeams(subProjectDto));
        projectRepository.save(parentProject);
        log.info("Update parent project with id = {} in DB", parentProject.getId());
        projectRepository.save(childProject);
        log.info("Saved child project with id = {} to DB", childProject.getId());
        return subProjectMapper.toDto(childProject);
    }

    private void validateVisibilityProjectAndSubProject(Project parentProject, Project childProject) {
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) &&
                childProject.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            log.warn("Not allowed to create private sub project in public project." +
                            " Parent project id {} is {} and sub project id {} is {}",
                    parentProject.getId(),
                    parentProject.getVisibility(),
                    childProject.getId(),
                    childProject.getVisibility()
            );
            throw new DataValidationException("Sub project can't be private in public project.");
        }
    }

    private List<Team> getTeams(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getTeamsIds() != null) {
            return teamRepository.findAllById(subProjectDto.getTeamsIds());
        }
        return null;
    }

    private List<Stage> getStages(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getStagesIds() != null) {
            return stageRepository.findAllById(subProjectDto.getStagesIds());
        } else return null;
    }
}
