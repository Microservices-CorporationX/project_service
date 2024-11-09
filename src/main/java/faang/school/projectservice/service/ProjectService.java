package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateReq;
import faang.school.projectservice.dto.project.ProjectFiltersReq;
import faang.school.projectservice.dto.project.ProjectPatchReq;
import faang.school.projectservice.dto.project.ProjectResp;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;

    public void createProject(ProjectCreateReq projectCreateReq) {
        checkProjectNameUniquenessForUser(projectCreateReq.name(), projectCreateReq.ownerId());
        Project project = projectMapper.mapProjectCreateReqToProject(projectCreateReq);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
    }

    @Transactional
    public void patchProject(ProjectPatchReq projectPatchReq) {
        Long projectId = projectPatchReq.id();
        Project project = projectRepository.getProjectById(projectId);
        projectMapper.patchProjectFromProjectPatchReq(projectPatchReq, project);
    }

    public List<ProjectResp> findProjectsWithFilters(ProjectFiltersReq projectFiltersReq) {
        List<Project> projects = projectRepository.findAll();
        Predicate<Project> projectFilter = constructFilter(projectFiltersReq);
        List<Project> filteredProjects = projects.stream().filter(projectFilter).toList();
        return projectMapper.mapProjectListToProjectRespList(filteredProjects);
    }

    public List<ProjectResp> findProjects() {
        return projectMapper.mapProjectListToProjectRespList(projectRepository.findAll());
    }

    public ProjectResp findProjectById(Long id) {
        return projectMapper.mapProjectToProjectResp(projectRepository.getProjectById(id));
    }

    private Predicate<Project> constructFilter(ProjectFiltersReq projectFiltersReq) {
        Predicate<Project> filter = constructFilterByProjectVisibility();
        String name = projectFiltersReq.name();
        ProjectStatus status = projectFiltersReq.status();
        if (name != null || status != null) {
            filter = constructFilterByNameAndStatus(filter, name, status);
        }
        return filter;
    }

    private Predicate<Project> constructFilterByProjectVisibility() {
        long userId = userContext.getUserId();
        return project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                isPrivateProjectAndUserIsTeamMember(userId, project);
    }

    private boolean isPrivateProjectAndUserIsTeamMember(long userId, Project project) {
        return project.getVisibility().equals(ProjectVisibility.PRIVATE) &&
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getUserId)
                        .toList().contains(userId);
    }

    private Predicate<Project> constructFilterByNameAndStatus(Predicate<Project> filter, String name, ProjectStatus status) {
        if(name != null) {
            filter = filter.and(project -> project.getName().toLowerCase().contains(name.toLowerCase()));
        }
        if(status != null) {
            filter = filter.and(project -> project.getStatus().equals(status));
        }
        return filter;
    }

    private void checkProjectNameUniquenessForUser(String name, Long ownerId) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.error("Project with name: {} already exists for this owner id: {}", name, ownerId);
            throw new IllegalArgumentException(String.format("Project with name: %s already exists for this owner id: %s", name, ownerId));
        }
    }
}
