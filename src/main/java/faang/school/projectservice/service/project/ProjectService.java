package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.validator.ValidatorForProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;
    private final List<ProjectUpdate> projectUpdates;
    private final List<ProjectFilter> projectFilters;

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
                .collect(Collectors.toList());

        projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .forEach(projectFilter -> projectFilter.apply(projects, projectFilterDto));


        return projects.stream()
                .map(projectMapper::toProjectDto)
                .toList();
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
