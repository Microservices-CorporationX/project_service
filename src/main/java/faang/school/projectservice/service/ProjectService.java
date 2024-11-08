package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<SubProjectFilter> subProjectFilters;
    private final SubProjectValidator subProjectValidator;

    public List<ProjectDto> findSubProjects(Long projectId, SubProjectFilterDto filters, Long userId) {
        List<Project> subProjects = projectRepository.getProjectById(projectId).getChildren().stream()
                .filter(project -> isProjectVisibleForUser(userId, project))
                .flatMap(project -> applyFilters(project, filters))
                .distinct()
                .toList();

        return projectMapper.toDtoList(subProjects);
    }

    public ProjectDto createSubProject(Long parentId, CreateSubProjectDto dto) {
        Project parentProject = projectRepository.getProjectById(parentId);
        subProjectValidator.validateOwnerExistence(dto.getOwnerId());
        subProjectValidator.validateExistenceByOwnerIdAndName(dto.getOwnerId(), dto.getName());
        subProjectValidator.validateSubProjectVisibility(parentProject.getVisibility(), dto.getVisibility());

        Project subProject = projectMapper.toEntity(dto);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        subProject = projectRepository.save(subProject);
        log.info("Sub project with id = '{}' was created", subProject.getId());

        return projectMapper.toDto(subProject);
    }

    public ProjectDto updateSubProject(Long parentId, Long childId, ProjectDto dto, Long userId) {
        Project parentProject = projectRepository.getProjectById(parentId);
        Project subProject = projectRepository.getProjectById(childId);

        subProjectValidator.validateSubProjectBelonging(parentId, subProject);
        subProjectValidator.validateOwnership(userId, subProject.getOwnerId());
        subProjectValidator.validateExistenceByOwnerIdAndName(dto.getOwnerId(), dto.getName());
        subProjectValidator.validateSubProjectStatus(subProject, dto.getStatus());
        if (isVisibilityChangedToPrivate(subProject.getVisibility(), dto.getVisibility())) {
            makeSubProjectsPrivate(subProject);
        } else {
            subProjectValidator.validateSubProjectVisibility(parentProject.getVisibility(), dto.getVisibility());
        }

        projectMapper.partialUpdate(subProject, dto);
        subProject = projectRepository.save(subProject);

        return projectMapper.toDto(subProject);
    }

    private void makeSubProjectsPrivate(Project subProject) {
        subProject.getChildren().forEach(child -> {
            child.setVisibility(ProjectVisibility.PRIVATE);
            child.getChildren().forEach(i -> makeSubProjectsPrivate(child));
        });
    }

    private Stream<Project> applyFilters(Project project, SubProjectFilterDto filters) {
        return subProjectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        Stream.of(project),
                        (stream, filter) -> filter.apply(stream, filters),
                        Stream::concat
                );
    }

    private boolean isProjectVisibleForUser(Long userId, Project project) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }

        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .anyMatch(userId::equals);
    }

    private boolean isVisibilityChangedToPrivate(ProjectVisibility projectVisibility, ProjectVisibility dtoVisibility) {
        return dtoVisibility == ProjectVisibility.PRIVATE && projectVisibility != dtoVisibility;
    }
}
