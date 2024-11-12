package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.CreateSubProjectDtoMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final CreateSubProjectDtoMapper createSubProjectDtoMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto dto) {
        Project project = projectMapper.toEntity(dto);
        project = projectRepository.save(project);
        log.info("Created project {}", project);
        return projectMapper.toDto(project);
    }

    public CreateSubProjectDto createSubProject(Long parentId, CreateSubProjectDto dto) {
        Project project = createSubProjectDtoMapper.toEntity(dto);
        Project parent = projectRepository.getProjectById(parentId);
        if (parent.getVisibility().equals(ProjectVisibility.PUBLIC)
                && project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            log.warn("Cannot create private subproject id: {} for public project id: {}"
                    , project.getId(), parent.getId());
            throw new DataValidationException("Cannot create private subproject for public project");
        }
        project.setParentProject(parent);
        parent.getChildren().add(project);
        project = projectRepository.save(project);
        projectRepository.save(parent);
        log.info("Created subproject {}", project);
        return createSubProjectDtoMapper.toDto(project);
    }

    public CreateSubProjectDto update(Long id, CreateSubProjectDto dto) {
        Project project = projectRepository.getProjectById(id);
        List<Project> children = project.getChildren();

        ProjectVisibility visibilityToUpdate = dto.getVisibility();
        if (!project.getVisibility().equals(visibilityToUpdate)) {
            updateVisibility(project, visibilityToUpdate, children);
        }
        ProjectStatus statusToUpdate = dto.getStatus();
        if (!project.getStatus().equals(statusToUpdate)) {
            updateStatus(project, statusToUpdate, children);
        }
        return createSubProjectDtoMapper.toDto(projectRepository.save(project));
    }

    public List<CreateSubProjectDto> getProjectsByFilters(ProjectFilterDto filterDto, Long projectId) {
        List<CreateSubProjectDto> children = projectRepository.getProjectById(projectId).getChildren()
                .stream()
                .filter(e -> e.getVisibility().equals(ProjectVisibility.PUBLIC))
                .filter(e -> ProjectFilter.applyAll(projectFilters, e, filterDto))
                .distinct()
                .map(createSubProjectDtoMapper::toDto)
                .toList();
        log.info("Found {} subprojects for project {}", children.size(), projectId);
        return children;
    }

    public void updateVisibility(Project project, ProjectVisibility visibilityToUpdate, List<Project> children) {
        project.setVisibility(visibilityToUpdate);
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Updated visibility for project id:{} is {}", project.getId(), visibilityToUpdate);
        if (children != null && !children.isEmpty() && visibilityToUpdate.equals(ProjectVisibility.PRIVATE)) {
            children.forEach(e -> updateVisibility(e, visibilityToUpdate, e.getChildren()));
            children.forEach(projectRepository::save);
            log.info("All children projects were updated with {} visibility", visibilityToUpdate);
        }
    }

    public void updateStatus(Project project, ProjectStatus statusToUpdate, List<Project> children) {
        if (statusToUpdate.equals(ProjectStatus.COMPLETED)){
            if (allChildProjectsCompleted(children) || children.isEmpty()) {
                String message = String.format("Project with id = %s has been completed", project.getId());
                Moment completed = new Moment();
                completed.setName(message);
                project.getMoments().add(completed);
                project.setStatus(statusToUpdate);
            } else {
                log.error("Project id:{} has unfinished subprojects", project.getId());
                throw new DataValidationException("Project has unfinished subprojects");
            }
        } else {
            project.setStatus(statusToUpdate);
            children.forEach(e -> updateStatus(e, statusToUpdate, e.getChildren()));
        }
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Updated status for project id:{} is {}", project.getId(), statusToUpdate);
    }

    public boolean allChildProjectsCompleted(List<Project> children) {
        return children.stream().allMatch(project -> project.getStatus().equals(ProjectStatus.COMPLETED));
    }
}
