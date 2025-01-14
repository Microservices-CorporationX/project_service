package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto, Long ownerId) {
        validateProjectNameUniqueness(ownerId, projectDto.getName());

        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
        Project existingProject = findProjectById(projectDto.getId());

        existingProject.setDescription(projectDto.getDescription());
        existingProject.setStatus(projectDto.getStatus());
        existingProject.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto, Long ownerId) {
        Project parentProject = findProjectById(createSubProjectDto.getParentProjectId());

        validateSubProjectCreation(parentProject, createSubProjectDto);

        Project subProject = projectMapper.toEntity(createSubProjectDto);
        subProject.setOwnerId(ownerId);
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setCreatedAt(LocalDateTime.now());
        subProject.setUpdatedAt(LocalDateTime.now());
        subProject.setParentProject(parentProject);

        Project savedSubProject = projectRepository.save(subProject);
        return projectMapper.toDto(savedSubProject);
    }

    public ProjectDto updateSubProject(ProjectDto projectDto) {
        Project existingSubProject = findProjectById(projectDto.getId());

        validateSubProjectStatusUpdate(existingSubProject, projectDto);

        existingSubProject.setStatus(projectDto.getStatus());
        existingSubProject.setVisibility(projectDto.getVisibility());
        existingSubProject.setUpdatedAt(LocalDateTime.now());

        Project updatedSubProject = projectRepository.save(existingSubProject);
        return projectMapper.toDto(updatedSubProject);
    }

    public List<ProjectDto> getSubProjects(Long parentProjectId, String name, ProjectStatus status, Long userId) {
        Project parentProject = findProjectById(parentProjectId);

        return parentProject.getChildren().stream()
                .filter(subProject -> (name == null || subProject.getName().contains(name)))
                .filter(subProject -> (status == null || subProject.getStatus() == status))
                .filter(subProject -> isProjectVisible(subProject, userId))
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getProjects(String name, ProjectStatus status, Long userId) {
        return projectRepository.findAll().stream()
                .filter(project -> (name == null || project.getName().contains(name)))
                .filter(project -> (status == null || project.getStatus() == status))
                .filter(project -> isProjectVisible(project, userId))
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public ProjectDto getProjectById(Long projectId, Long userId) {
        Project project = findProjectById(projectId);

        if (!isProjectVisible(project, userId)) {
            throw new IllegalAccessException("You don't have access to this project");
        }

        return projectMapper.toDto(project);
    }

    private void validateProjectNameUniqueness(Long ownerId, String name) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            throw new IllegalArgumentException("Project with the same name already exists for this owner");
        }
    }

    private void validateSubProjectCreation(Project parentProject, CreateSubProjectDto createSubProjectDto) {
        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && createSubProjectDto.getVisibility() == ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException("Cannot create a public subproject for a private parent project");
        }
    }

    private void validateSubProjectStatusUpdate(Project subProject, ProjectDto projectDto) {
        if (projectDto.getStatus() == ProjectStatus.COMPLETED) {
            boolean hasOpenSubProjects = subProject.getChildren().stream()
                    .anyMatch(child -> child.getStatus() != ProjectStatus.COMPLETED);
            if (hasOpenSubProjects) {
                throw new IllegalStateException("Cannot close project while it has open subprojects");
            }
        }
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private boolean isProjectVisible(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }
        return project.getOwnerId().equals(userId);
    }
}