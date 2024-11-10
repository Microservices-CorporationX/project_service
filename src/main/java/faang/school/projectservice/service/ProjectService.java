package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        log.info("Creating project with name: {} for owner ID: {}", projectDto.getName(), projectDto.getOwnerId());
        validateProjectNameUniqueness(projectDto.getOwnerId(), projectDto.getName());

        Project project = Project.builder()
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .ownerId(projectDto.getOwnerId())
                .status(ProjectStatus.CREATED)
                .visibility(projectDto.getVisibility() != null ? projectDto.getVisibility() : ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();

        project = projectRepository.save(project);
        log.info("Project created with ID: {}", project.getId());
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        log.info("Updating project with ID: {}", projectId);
        Project project = projectRepository.getProjectById(projectId);


        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setVisibility(projectDto.getVisibility() != null ? projectDto.getVisibility() : project.getVisibility());

        if (projectDto.getStatus() != null) {
            project.setStatus(projectDto.getStatus());
        }

        project.setUpdatedAt(LocalDateTime.now());

        project = projectRepository.save(project);
        log.info("Project with ID: {} updated successfully", projectId);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> findProjects(String name, ProjectStatus status, ProjectVisibility visibility, Long userId) {
        log.info("Finding projects with filters - Name: {}, Status: {}, Visibility: {}", name, status, visibility);
        ProjectFilter filter = new ProjectFilter(name, status, visibility, userId);
        return projectRepository.findAll().stream()
                .filter(filter::apply)
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getAllProjects() {
        log.info("Retrieving all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long projectId) {
        log.info("Retrieving project with ID: {}", projectId);
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    private void validateProjectNameUniqueness(Long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.warn("Project with name: {} already exists for owner ID: {}", name, ownerId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project with the same name already exists for this owner.");
        }
    }

    private void updateProjectDetails(Project project, String description, ProjectStatus status) {
        project.setDescription(description);
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
    }
}
