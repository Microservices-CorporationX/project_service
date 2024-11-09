package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final List<Filter<Project, ProjectFilterDto>> projectFilters;

    public ProjectDto getById(Long projectId) {
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public ProjectDto createSubProject(Long parentId, CreateProjectDto createProjectDto) {

        return null;
    }

    public ProjectDto updateSubProject(Long parentId, UpdateSubProjectDto updateSubProjectDto) {
        projectValidator.validateProjectExistsById(updateSubProjectDto.getId());

        return null;
    }

    public List<ProjectDto> filterSubProjects(Long parentId, ProjectFilterDto filters) {

        Project project = projectRepository.getProjectById(parentId);
        Stream<Project> childrenProjectsStream;

        if (projectValidator.isProjectPublic(project)) {
            childrenProjectsStream = project
                    .getChildren()
                    .stream()
                    .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC);
        } else {
            childrenProjectsStream = Stream.empty();
        }

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(childrenProjectsStream, filters))
                .map(projectMapper::toDto)
                .toList();
    }

    private void changeStatus(Project project, UpdateSubProjectDto updateSubProjectDto) {
        projectValidator.validateSameProjectStatus(project, updateSubProjectDto);
        projectValidator.validateProjectStatusCompletedOrCancelled(project);

        if (updateSubProjectDto.getStatus() == ProjectStatus.IN_PROGRESS) {
            applyInProgressStatus(project);
        } else if (updateSubProjectDto.getStatus() == ProjectStatus.ON_HOLD) {
            applyOnHoldStatus(project);
        } else if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            applyCompletedStatus(project);
        } else if (updateSubProjectDto.getStatus() == ProjectStatus.CANCELLED) {
            applyCancelledStatus(project);
        }
    }

    private void applyInProgressStatus(Project project) {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        if (projectValidator.hasParentProject(project)) {
            Project parentProject = project.getParentProject();
            applyInProgressStatus(parentProject);
        }
        projectRepository.save(project);
    }

    private void applyOnHoldStatus(Project project) {
        projectValidator.validateProjectStatusValidToHold(project);
        project.setStatus(ProjectStatus.ON_HOLD);
        if (projectValidator.hasChildrenProjects(project)) {
            project.getChildren().forEach(this::applyOnHoldStatus);
        }
        projectRepository.save(project);
    }

    private void applyCancelledStatus(Project project) {
        project.setStatus(ProjectStatus.CANCELLED);
        if (projectValidator.hasChildrenProjects(project)) {
            project.getChildren().forEach(this::applyCancelledStatus);
        }
        projectRepository.save(project);
    }

    private void applyCompletedStatus(Project project) {
        projectValidator.validateProjectIsValidToComplete(project);
        project.setStatus(ProjectStatus.COMPLETED);
        projectRepository.save(project);
    }


}
