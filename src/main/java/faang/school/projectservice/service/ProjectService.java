package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

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
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto updateStatus(ProjectStatus status, long projectId) {
        try {
            Project project = getProjectForOwner(projectId);
            project.setStatus(status);
            project.setUpdatedAt(LocalDateTime.now());
            return projectMapper.toDto(projectRepository.save(project));
        } catch (NotOwnerException e) {
            throw new DataValidationException("Must be the owner to change status");
        }
    }

    public ProjectDto updateDescription(String description, long projectId) {
        try {
            Project project = getProjectForOwner(projectId);
            project.setDescription(description);
            project.setUpdatedAt(LocalDateTime.now());
            return projectMapper.toDto(projectRepository.save(project));
        } catch (NotOwnerException e) {
            throw new DataValidationException("Must be the owner to change description");
        }
    }

    public List<ProjectDto> findWithFilters(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        Stream<Project> filtered = filters.stream().filter(filter -> filter.isApplicable(projectFilterDto))
                .flatMap(filter -> filter.apply(projects, projectFilterDto));
        return filtered.filter(this::filterPrivate).map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> findAll() {
        Stream<Project> projects = projectRepository.findAll().stream().filter(this::filterPrivate);
        return projects.map(projectMapper::toDto).toList();
    }

    public Optional<ProjectDto> findById(long id) {
        Project project = projectRepository.getProjectById(id);
        return project == null ? Optional.empty() : Optional.of(projectMapper.toDto(project));
    }

    private void validateName(String projectName, Long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectName)) {
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

    private static class NotOwnerException extends Exception {
        public NotOwnerException(String msg) {
            super(msg);
        }
    }
}
