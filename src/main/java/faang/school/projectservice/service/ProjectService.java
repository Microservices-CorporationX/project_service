package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
        logUpdate(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto updateStatus(ProjectStatus status, long projectId) {
        try {
            Project project = getProjectForOwner(projectId);
            project.setStatus(status);
            project.setUpdatedAt(LocalDateTime.now());
            project = projectRepository.save(project);
            logUpdate(project);
            return projectMapper.toDto(project);
        } catch (NotOwnerException e) {
            log.info("User with id {} tried to change someone else's project status, project id is {}",
                    userContext.getUserId(), projectId);
            throw new DataValidationException("Must be the owner to change status");
        }
    }

    public ProjectDto updateDescription(String description, long projectId) {
        try {
            Project project = getProjectForOwner(projectId);
            project.setDescription(description);
            project.setUpdatedAt(LocalDateTime.now());
            logUpdate(project);
            return projectMapper.toDto(projectRepository.save(project));
        } catch (NotOwnerException e) {
            log.info("User with id {} tried to change someone else's project description, project id is {}",
                    userContext.getUserId(), projectId);
            throw new DataValidationException("Must be the owner to change description");
        }
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
        return project == null || !filterPrivate(project) ? Optional.empty() : Optional.of(projectMapper.toDto(project));
    }

    private void validateName(String projectName, Long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectName)) {
            log.info("User with id {} tried to create a project with the same name", userContext.getUserId());
            throw new DataValidationException("Can not create new project with this project name, " +
                    "this name is already used for another project of this user");
        }
    }

    private Project getProjectForOwner(long projectId) throws NotOwnerException {
        Long userId = userContext.getUserId();
        Project project = projectRepository.getProjectById(projectId);
        if (!Objects.equals(project.getOwnerId(), userId)) {
            throw new NotOwnerException("The user is not the owner of this project");
        }
        return project;
    }

    private boolean filterPrivate(Project project) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            Long userId = userContext.getUserId();
            List<Team> teams = project.getTeams();
            List<TeamMember> members;
            if (teams != null) {
                members = project.getTeams().stream()
                        .filter(team -> team.getTeamMembers() != null)
                        .flatMap(team -> team.getTeamMembers().stream()).toList();
            } else {
                members = new ArrayList<>();
            }
            Set<Long> memberIds = new HashSet<>();
            memberIds.add(project.getOwnerId());
            members.forEach(member -> memberIds.add(member.getUserId()));
            return memberIds.contains(userId);
        }
        return true;
    }

    private void logUpdate(Project project) {
        log.info("User with id {} updated project {}", userContext.getUserId(), project);
    }

    private static class NotOwnerException extends Exception {
        public NotOwnerException(String msg) {
            super(msg);
        }
    }
}
