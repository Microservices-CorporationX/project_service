package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filters.project.ProjectFilter;
import faang.school.projectservice.mapper.project.CreateSubProjectMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.helpers.ProjectSearcher;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserContext userContext;
    private final ProjectMapper projectMapper;
    private final CreateSubProjectMapper createSubProjectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> filters;
    private final ProjectValidator projectValidator;
    private final MomentService momentService;

    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.validateUniqueProject(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(userContext.getUserId());
        project.setStatus(ProjectStatus.CREATED);
        projectValidator.validateName(project.getName(), project.getOwnerId());
        LocalDateTime currentTime = LocalDateTime.now();
        project.setCreatedAt(currentTime);
        project.setUpdatedAt(currentTime);
        project.setChildren(new ArrayList<>());
        project = projectRepository.save(project);
        log.info("User with id {} created a project {}", userContext.getUserId(), project);
        return projectMapper.toDto(project);
    }

    public CreateSubProjectDto createSubProject(CreateSubProjectDto projectDto) {
        projectValidator.validateUniqueProject(projectDto);
        Project project = createSubProjectMapper.toEntity(projectDto);
        Project parent = projectRepository.getProjectById(projectDto.getParentId());
        projectValidator.validateIsPublic(parent, project);
        project.setOwnerId(userContext.getUserId());
        project.setStatus(ProjectStatus.CREATED);
        project.setParentProject(parent);
        parent.getChildren().add(project);
        LocalDateTime currentTime = LocalDateTime.now();
        project.setCreatedAt(currentTime);
        project.setUpdatedAt(currentTime);
        project.setChildren(new ArrayList<>());
        project = projectRepository.save(project);
        projectRepository.save(parent);
        log.info("User with id {} created subproject {}", userContext.getUserId(), project);
        return createSubProjectMapper.toDto(project);
    }

    public CreateSubProjectDto updateSubProject(CreateSubProjectDto projectDto) {
        projectValidator.validateProjectExists(projectDto);
        Project project = projectRepository.getProjectById(projectDto.getId());
        List<Project> children = project.getChildren();

        if (projectValidator.needToUpdateVisibility(project, projectDto)) {
            updateSubProjectsVisibility(project, projectDto.getVisibility(), children);
        }

        if (projectValidator.needToUpdateStatus(project, projectDto)) {
            updateSubProjectsStatus(project, projectDto.getStatus(), children);
        }
        return createSubProjectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto updateStatus(ProjectStatus status, long projectId) {
        Project project = getProjectForOwner(projectId);
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        logUpdate(project);
        return projectMapper.toDto(project);
    }

    public void updateSubProjectsStatus(Project project, ProjectStatus statusToUpdate, List<Project> children) {
        projectValidator.validateProjectAlreadyCompleted(project);
        if (statusToUpdate == ProjectStatus.COMPLETED){
            if (projectValidator.validateAllChildProjectsCompleted(project)) {
                MomentDto momentDto = MomentDto.builder()
                        .name(project.getName() + " completed")
                        .description("Project with id: " + project.getId() + " has been completed")
                        .date(LocalDateTime.now())
                        .projectIds(List.of(project.getId()))
                        .build();
                momentService.createMoment(momentDto);
            }
        } else {
            children.forEach(e -> updateSubProjectsStatus(e, statusToUpdate, e.getChildren()));
        }
        project.setStatus(statusToUpdate);
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Updated status for project id:{} is {}", project.getId(), statusToUpdate);
    }

    public void updateSubProjectsVisibility(Project project, ProjectVisibility visibilityToUpdate, List<Project> children) {
        project.setVisibility(visibilityToUpdate);
        project.setUpdatedAt(LocalDateTime.now());
        log.info("Updated visibility for project id:{} is {}", project.getId(), visibilityToUpdate);
        if (children != null && !children.isEmpty() && visibilityToUpdate.equals(ProjectVisibility.PRIVATE)) {
            children.forEach(e -> updateSubProjectsVisibility(e, visibilityToUpdate, e.getChildren()));
            children.forEach(projectRepository::save);
            log.info("All children projects were updated with {} visibility", visibilityToUpdate);
        }
    }

    public ProjectDto updateDescription(String description, long projectId) {
        Project project = getProjectForOwner(projectId);
        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());
        logUpdate(project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    public List<ProjectDto> findWithFilters(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        log.info("User with id {} requested filtered projects", userContext.getUserId());
        return filters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .reduce(projects, (stream, filter) -> filter.apply(stream, projectFilterDto), (s1, s2) -> s1)
                .filter(this::filterPrivate)
                .map(projectMapper::toDto)
                .toList();
    }

    public List<CreateSubProjectDto> getProjectsByFilters(Long projectId, ProjectFilterDto filterDto) {
        Stream<Project> children = projectRepository.getProjectById(projectId).getChildren().stream();
        return filters.stream()
                .filter(e -> e. isApplicable(filterDto))
                .reduce(children, (stream, filter) -> filter.apply(stream, filterDto),
                        ((subStream, stream) -> stream))
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .distinct()
                .map(createSubProjectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> findAll() {
        Stream<Project> projects = projectRepository.findAll().stream().filter(this::filterPrivate);
        log.info("User with id {} requested all projects", userContext.getUserId());
        return projects.map(projectMapper::toDto).toList();
    }

    public Optional<ProjectDto> findById(long id) {
        Project project = projectRepository.getProjectById(id);
        if (filterPrivate(project)) {
            log.info("User with id {} requested project with id {}", userContext.getUserId(), id);
            return Optional.of(projectMapper.toDto(project));
        }
        return Optional.empty();
    }

    private Project getProjectForOwner(long projectId) {
        Long userId = userContext.getUserId();
        Project project = projectRepository.getProjectById(projectId);
        if (!Objects.equals(project.getOwnerId(), userId)) {
            log.info("User with id {} tried to change someone else's project description, project id is {}",
                    userContext.getUserId(), projectId);
            throw new DataValidationException("The user is not the owner of this project");
        }
        return project;
    }

    private boolean filterPrivate(Project project) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            Long userId = userContext.getUserId();
            List<Project> allProjects = ProjectSearcher.findAllProjects(project);
            List<Team> teams = allProjects.stream()
                    .flatMap(currentProject -> {
                        if (currentProject.getTeams() != null) {
                            return currentProject.getTeams().stream();
                        }
                        return Stream.of();
                    }).toList();
            List<TeamMember> members = teams.stream()
                    .filter(team -> team.getTeamMembers() != null)
                    .flatMap(team -> team.getTeamMembers().stream()).toList();
            Set<Long> memberIds = members.stream().map(TeamMember::getUserId).collect(Collectors.toSet());
            return userId.equals(project.getOwnerId()) || memberIds.contains(userId);
        }
        return true;
    }

    private void logUpdate(Project project) {
        log.info("User with id {} updated project {}", userContext.getUserId(), project);
    }
}
