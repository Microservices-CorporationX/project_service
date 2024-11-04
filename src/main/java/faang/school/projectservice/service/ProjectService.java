package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.ProjectDto;
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
    public ProjectDto createProject(String name, String description, Long ownerId) {
        log.info("Creating project with name: {} for owner ID: {}", name, ownerId);
        validateProjectNameUniqueness(ownerId, name);

        Project project = Project.builder()
                .name(name)
                .description(description)
                .ownerId(ownerId)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();

        project = projectRepository.save(project);
        log.info("Project created with ID: {}", project.getId());
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, String description, String status) {
        log.info("Updating project with ID: {}", projectId);
        Project project = projectRepository.getProjectById(projectId);
        updateProjectDetails(project, description, status);
        project = projectRepository.save(project);
        log.info("Project with ID: {} updated successfully", projectId);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> findProjects(String name, ProjectStatus status, Long userId) {
        log.info("Finding projects with filters - Name: {}, Status: {}", name, status);
        return projectRepository.findAll().stream()
                .filter(project -> (name == null || project.getName().equals(name)) &&
                        (status == null || project.getStatus() == status) &&
                        (project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(userId)))
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
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        return projectMapper.toDto(project);
    }

    private void validateProjectNameUniqueness(Long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.warn("Project with name: {} already exists for owner ID: {}", name, ownerId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Project with the same name already exists for this owner.");
        }
    }

    private void updateProjectDetails(Project project, String description, String status) {
        project.setDescription(description);
        try{
            project.setStatus(ProjectStatus.valueOf(status));
        } catch (IllegalArgumentException e){
            log.error("Invalid project status provided: {}", status);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid project status provided.");
        }
        project.setUpdatedAt(LocalDateTime.now());
    }
}