package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.UpdateSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;
    private final ProjectMomentMapper projectMomentMapper;

    private final ProjectValidator projectValidator;

    private final StageService stageService;
    private final MomentService momentService;

    private final List<SubProjectFilter> filters;

    @Transactional
    public ProjectDto createSubProject(long parentProjectId,
                                       CreateSubProjectDto createDto) {
        log.info("Trying to create a sub project: {} for the project: {}",
                createDto, parentProjectId);
        Project parentProject = getProjectById(parentProjectId);
        projectValidator.validateCreateSubProject(parentProject, createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject.setParentProject(parentProject);
        parentProject.addChildren(subProject);

        projectRepository.save(subProject);

        List<StageDto> stagesDto = createDto.stages();
        mapStages(subProject, stagesDto);

        log.info("Successfully created a sub project: {} for project: {}",
                createDto, parentProjectId);
        return projectMapper.toDto(subProject);
    }

    @Transactional
    public ProjectDto updateSubProject(long projectId,
                                       UpdateSubProjectDto updateDto) {
        log.info("Trying to update a sub project: {} with the following parameters: {}",
                projectId, updateDto);
        Project subProject = getProjectById(projectId);
        projectValidator.validateUpdateSubProject(subProject, updateDto);

        projectMapper.update(updateDto, subProject);
        subProject.setUpdatedAt(LocalDateTime.now());
        if (subProject.isPrivate() && subProject.hasChildren()) {
            subProject.setPrivateVisibility();
        }

        if (subProject.isCompleted()) {
            Moment moment = projectMomentMapper.toMoment(subProject);
            moment.addProject(subProject);
            subProject.addMoment(moment);
            momentService.createMoment(moment);
        }

        log.info("Successfully updated a sub project: {}", projectId);
        return projectMapper.toDto(subProject);
    }

    @Transactional
    public List<ProjectDto> getFilteredSubProjects(long parentProjectId,
                                                   SubProjectFilterDto filterDto) {
        log.info("Trying to get sub projects for project: {} with the following filters: {}",
                parentProjectId, filterDto);
        Project parentProject = getProjectById(parentProjectId);

        if (parentProject.getChildren() == null) {
            log.info("Project: {} has no sub projects. Returning empty list", parentProjectId);
            return new ArrayList<>();
        }

        List<Project> subProjects = parentProject.getChildren();
        for (SubProjectFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                subProjects = filter.apply(subProjects, filterDto);
            }
        }

        log.info("Successfully got sub projects for project: {} with the following filters: {}",
                parentProjectId, filterDto);
        return projectMapper.toDto(subProjects);
    }

    private Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    private void mapStages(Project subProject, List<StageDto> stagesDto) {
        if (stagesDto != null) {
            List<Stage> stages = stageService.getMappedStages(stagesDto);
            stages.forEach(stage -> {
                stage.setProject(subProject);
                stageService.createStage(stage);
            });
            subProject.setStages(stages);
        }
    }
}
