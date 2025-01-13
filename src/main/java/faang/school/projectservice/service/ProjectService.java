package faang.school.projectservice.service;

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

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto, Long ownerId) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectDto.getName())) {
            throw new IllegalArgumentException("Project with the same name already exists for this owner");
        }

        Project project = projectMapper.toEntity(projectDto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        existingProject.setDescription(projectDto.getDescription());
        existingProject.setStatus(projectDto.getStatus());
        existingProject.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(existingProject);

        return projectMapper.toDto(updatedProject);
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
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!isProjectVisible(project, userId)) {
            throw new IllegalAccessException("You don't have access to this project");
        }

        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getProjectsByFilter(String name, ProjectStatus status, Long userId) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> (name == null || project.getName().contains(name)))
                .filter(project -> (status == null || project.getStatus() == status))
                .filter(project -> (project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(userId)))
                .toList();

        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    private boolean isProjectVisible(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }
        return project.getOwnerId().equals(userId);
    }
}