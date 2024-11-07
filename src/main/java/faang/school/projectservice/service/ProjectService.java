package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;
    private final ProjectMapper projectMapper;
    private final List<ProjectUpdate> projectUpdates;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto getProjectById(long id) {
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    public List<ProjectDto> getAllProjects(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .flatMap(filter -> filter.apply(projectFilterDto, projects))
                .filter(project -> isUserMemberOfPrivateProject(project, userContext.getUserId()))
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidator.validate(projectDto, this::existsByOwnerUserIdAndName, userContext.getUserId());

        Project project = projectMapper.toEntity(projectDto);

        return projectMapper.toDto(projectRepository.create(project));
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());

        projectUpdates.stream()
                .filter(update -> update.isApplicable(projectDto))
                .forEach(update -> update.apply(projectDto, project));

        project.setUpdatedAt(LocalDateTime.now());

        return projectMapper.toDto(projectRepository.save(project));
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
}
