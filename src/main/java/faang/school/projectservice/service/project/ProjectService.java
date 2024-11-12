package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.project.ProjectFilter;
import faang.school.projectservice.helpers.ProjectDfs;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> filters;

    public ProjectDto create(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(userContext.getUserId());
        project.setStatus(ProjectStatus.CREATED);
        validateName(project.getName(), project.getOwnerId());
        LocalDateTime currentTime = LocalDateTime.now();
        project.setCreatedAt(currentTime);
        project.setUpdatedAt(currentTime);
        project = projectRepository.save(project);
        log.info("User with id {} created a project {}", userContext.getUserId(), project);
        return projectMapper.toDto(project);
    }

    public ProjectDto updateStatus(ProjectStatus status, long projectId) {
        Project project = getProjectForOwner(projectId);
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        logUpdate(project);
        return projectMapper.toDto(project);
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

    public List<ProjectDto> findAll() {
        Stream<Project> projects = projectRepository.findAll().stream().filter(this::filterPrivate);
        log.info("User with id {} requested all projects", userContext.getUserId());
        return projects.map(projectMapper::toDto).toList();
    }

    public Optional<ProjectDto> findById(long id) {
        Project project = projectRepository.getProjectById(id);
        log.info("User with id {} requested project with id {}", userContext.getUserId(), id);
        return Optional.of(projectMapper.toDto(project));
    }

    private void validateName(String projectName, Long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectName)) {
            log.info("User with id {} tried to create a project with the same name", userContext.getUserId());
            throw new DataValidationException("Can not create new project with this project name, " +
                    "this name is already used for another project of this user");
        }
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
            List<Project> allProjects = ProjectDfs.findAllProjects(project);
            List<Team> teams = allProjects.stream()
                    .flatMap(currentProject -> {
                        if (currentProject.getTeams() != null) {
                            return currentProject.getTeams().stream();
                        }
                        return Stream.of();
                    }).toList();
            List<TeamMember> members;
            members = teams.stream()
                    .filter(team -> team.getTeamMembers() != null)
                    .flatMap(team -> team.getTeamMembers().stream()).toList();
            Set<Long> memberIds = members.stream().map(TeamMember::getId).collect(Collectors.toSet());
            return userId.equals(project.getOwnerId()) || memberIds.contains(userId);
        }
        return true;
    }

    private void logUpdate(Project project) {
        log.info("User with id {} updated project {}", userContext.getUserId(), project);
    }
}
