package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.fillters.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto, long ownerId) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectDto.getName())) {
            throw new IllegalArgumentException("Проект с таким названием уже существует для этого владельца");
        }

        Project project = projectMapper.toEntity(projectDto);

        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PRIVATE);
        }

        projectRepository.save(project);
        log.info("Проект {} сохранен", project.getName());
        return projectMapper.toProjectDto(project);
    }

    public ProjectDto updateProject(ProjectDto projectDto, long userId) {
        Project project = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Не существует проекта с ID: " + projectDto.getId()));

        if (!project.getOwnerId().equals(userId)) {
            throw new SecurityException("У вас нет разрешения на обновление этого проекта");
        }

        project.setStatus(projectDto.getStatus());
        project.setDescription(projectDto.getDescription());
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        log.info("Проект {} обновлен", project.getName());
        return projectMapper.toProjectDto(project);
    }

    public List<ProjectDto> getAllProjectsWithFilters(ProjectFilterDto filters, long userId) {
        List<Project> projects = projectRepository.findAll();

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(filters)) {
                projects = filter.apply(projects, filters);
            }
        }

        return getListProjects(userId, projects);
    }

    public List<ProjectDto> getAllProjects(long userId){
        List<Project> projects = projectRepository.findAll();
        return getListProjects(userId, projects);
    }

    public ProjectDto getProjectById(long projectId, long userId){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new NoSuchElementException("Не существует проекта с ID: " + projectId));

        if(!isVisibleToUser(project,userId)){
            throw new SecurityException("У вас нет доступа к проекту с id: " + projectId);
        }

        return projectMapper.toProjectDto(project);
    }

    private List<ProjectDto> getListProjects(long userId, List<Project> projects) {
        return projects.stream()
                .filter(project -> isVisibleToUser(project, userId))
                .map(projectMapper::toProjectDto)
                .toList();
    }

    private boolean isVisibleToUser(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            return isParticipant(project, userId);
        }
        return true;
    }

    private boolean isParticipant(Project project, Long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(member -> member.getId().equals(userId));
    }

}
