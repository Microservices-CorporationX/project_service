package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.CreateSubProjectMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final CreateSubProjectMapper createSubProjectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> projectFilters;
    private final ProjectValidator projectValidator;

    public ProjectDto createProject(ProjectDto dto) {
        projectValidator.validateUniqueProject(dto);
        Project project = projectMapper.toEntity(dto);
        project.setCreatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        log.info("Created project {}", project);
        return projectMapper.toDto(project);
    }

    public CreateSubProjectDto createSubProject(Long parentId, CreateSubProjectDto dto) {
        projectValidator.validateUniqueProject(dto);
        Project project = createSubProjectMapper.toEntity(dto);
        Project parent = projectRepository.getProjectById(parentId);
        projectValidator.validateIsPublic(parent, project);
        project.setParentProject(parent);
        project.setCreatedAt(LocalDateTime.now());
        parent.getChildren().add(project);
        project = projectRepository.save(project);
        projectRepository.save(parent);
        log.info("Created subproject {}", project);
        return createSubProjectMapper.toDto(project);
    }

    public CreateSubProjectDto update(CreateSubProjectDto dto) {
        projectValidator.validateProjectExists(dto);
        Project project = projectRepository.getProjectById(dto.getId());
        List<Project> children = project.getChildren();

        if (projectValidator.needToUpdateVisibility(project, dto)) {
            updateVisibility(project, dto.getVisibility(), children);
        }

        if (projectValidator.needToUpdateStatus(project, dto)) {
            updateStatus(project, dto.getStatus(), children);
        }
        return createSubProjectMapper.toDto(projectRepository.save(project));
    }

    public List<CreateSubProjectDto> getProjectsByFilters(Long projectId, ProjectFilterDto filterDto) {
        Stream<Project> children = projectRepository.getProjectById(projectId).getChildren().stream();
        return projectFilters.stream()
                .filter(e -> e. isApplicable(filterDto))
                .flatMap(filter -> filter.apply(children, filterDto))
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .distinct()
                .map(createSubProjectMapper::toDto)
                .toList();
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
        projectValidator.validateProjectAlreadyCompleted(project);
        if (statusToUpdate == ProjectStatus.COMPLETED){
            if (projectValidator.validateAllChildProjectsCompleted(project)) {
                String message = String.format("Project with id = %s has been completed", project.getId());
                Moment completed = new Moment();
                completed.setName(message);
                project.getMoments().add(completed);
            }
        } else {
            children.forEach(e -> updateStatus(e, statusToUpdate, e.getChildren()));
        }
        project.setStatus(statusToUpdate);
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Updated status for project id:{} is {}", project.getId(), statusToUpdate);
    }
}
