package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ValidatorForProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;
    private final List<ProjectUpdate> projectUpdates;
    private final List<ProjectFilter> projectFilters;
    private final ProjectValidator projectValidator;
    private final UserContext userContext;

    public ProjectDto createSubProject(Long projectId, CreateSubProjectDto createSubProjectDto) {
        Project mainProject = getProject(projectId);

        ValidatorForProjectService.checkVisibility(
                mainProject.getVisibility(),
                createSubProjectDto.getVisibility()
        );
        ValidatorForProjectService.checkProjectContainsSubProject(mainProject, createSubProjectDto.getName());

        Project subProject = subProjectMapper.toProject(createSubProjectDto);
        addDependency(mainProject, subProject);
        projectRepository.save(subProject);
        projectRepository.save(mainProject);

        return projectMapper.toProjectDto(subProject);
    }

    public ProjectDto updateSubProject(ProjectDto subProjectDto) {
        ValidatorForProjectService.checkValidId(subProjectDto.getId());
        Project subProject = getProject(subProjectDto.getId());

        boolean isAllSubProjectCancelled = checkCancelledStatus(subProjectDto);
        ValidatorForProjectService.checkSubProjectStatus(subProject.getStatus(), isAllSubProjectCancelled);

        projectUpdates.stream()
                .filter(projectUpdate -> projectUpdate.isApplicable(subProjectDto))
                .forEach(projectUpdate -> projectUpdate.apply(subProject, subProjectDto));

        if (isAllSubProjectCancelled) {
            createCancelledMoment(subProject);
        }

        subProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(subProject);

        return projectMapper.toProjectDto(subProject);
    }

    public List<ProjectDto> getSubProjects(Long projectId, ProjectFilterDto projectFilterDto) {
        Project project = getProject(projectId);

        ValidatorForProjectService.checkProjectContainsChild(project);
        List<Project> projects = project.getChildren().stream()
                .filter(subProject -> isPrivateProjectInPublic(project, subProject))
                .toList();

        projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .forEach(projectFilter -> projectFilter.apply(projectFilterDto, projects.stream()));


        return projects.stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public ProjectDto getProjectById(long id) {
        return projectMapper.toProjectDto(projectRepository.getProjectById(id));
    }

    public List<ProjectDto> getAllProjects(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> isUserMemberOfPrivateProject(project, userContext.getUserId()))
                .toList();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .reduce(projects.stream(),
                        (stream, filter) -> filter.apply(projectFilterDto, stream),
                        (s1, s2) -> s1)
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidator.validate(projectDto, this::existsByOwnerUserIdAndName, userContext.getUserId());

        Project project = projectMapper.toProject(projectDto);

        return projectMapper.toProjectDto(projectRepository.create(project));
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
        projectValidator.validate(projectDto, this::existsByOwnerUserIdAndName, userContext.getUserId());
        Project project = projectRepository.getProjectById(projectDto.getId());

        projectMapper.update(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());

        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    public boolean existsByOwnerUserIdAndName(Long userId, String projectName) {
        return projectRepository.existsByOwnerUserIdAndName(userId, projectName);
    }

    private boolean isUserMemberOfPrivateProject(Project project, long userId) {
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            if (project.getTeams() != null) {
                return project.getTeams().stream()
                        .filter(Objects::nonNull)
                        .anyMatch(team -> team.getTeamMembers() != null && team.getTeamMembers().stream()
                                .filter(Objects::nonNull)
                                .anyMatch(teamMember -> teamMember.getUserId() == userId));
            }
            return false;
        }
        return true;
    }

    public Project getProjectEntityById(Long id) {
        return projectRepository.getProjectById(id);
    }

    private boolean checkCancelledStatus(ProjectDto subProjectDto) {
        if (subProjectDto.getChildrenIds() == null || subProjectDto.getChildrenIds().isEmpty()) {
            return true;
        }
        return subProjectDto.getChildrenIds().stream()
                .map(projectRepository::getProjectById)
                .allMatch(project -> project.getStatus() == ProjectStatus.CANCELLED);
    }

    private void addDependency(Project parent, Project child) {
        if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
        }
        child.setChildren(new ArrayList<>());
        child.setTeams(new ArrayList<>());

        child.setParentProject(parent);
        parent.getChildren().add(child);
    }

    private void createCancelledMoment(Project project) {
        Moment allSubProjectCancelled = new Moment();

        allSubProjectCancelled.setName("allSubProjectCancelled");
        allSubProjectCancelled.setDescription("Все дочерние проекты были закрыты");
        allSubProjectCancelled.setCreatedAt(LocalDateTime.now());
        allSubProjectCancelled.setProjects(project.getChildren());

        if (project.getTeams() != null) {
            allSubProjectCancelled.setUserIds(project.getTeams().stream()
                    .filter(Objects::nonNull)
                    .flatMap(team -> team.getTeamMembers().stream()
                            .filter(Objects::nonNull)
                            .map(TeamMember::getId))
                    .toList());
        }

        if (project.getMoments() == null) {
            project.setMoments(new ArrayList<>());
        }

        project.getMoments().add(allSubProjectCancelled);
    }

    private boolean isPrivateProjectInPublic(Project mainProject, Project subProject) {
        return !(mainProject.getVisibility() == ProjectVisibility.PUBLIC
                && subProject.getVisibility() == ProjectVisibility.PRIVATE);
    }

    private Project getProject(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
