package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponseDto createProject(CreateProjectRequestDto projectRequestDto, Long ownerId) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectRequestDto.getName())) {
            throw new IllegalArgumentException("Project with the same name already exists for this owner");
        }

        Project project = projectMapper.toEntity(projectRequestDto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDto(savedProject);
    }

    public ProjectResponseDto updateProject(Long projectId, CreateProjectRequestDto projectRequestDto) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        existingProject.setDescription(projectRequestDto.getDescription());
        existingProject.setName(projectRequestDto.getName());
        existingProject.setVisibility(projectRequestDto.getVisibility());
        existingProject.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toResponseDto(updatedProject);
    }

    public List<ProjectResponseDto> getProjects(String name, ProjectStatus status, Long userId) {
        return projectRepository.findAll().stream()
                .filter(project -> (name == null || (project.getName() != null && project.getName().contains(name))))
                .filter(project -> (status == null || project.getStatus() == status))
                .filter(project -> isProjectVisible(project, userId))
                .map(projectMapper::toResponseDto)
                .toList();
    }

    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponseDto)
                .toList();
    }

    public ProjectResponseDto getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!isProjectVisible(project, userId)) {
            throw new IllegalArgumentException("You don't have access to this project");
        }

        return projectMapper.toResponseDto(project);
    }

    private boolean isProjectVisible(Project project, Long userId) {
        return project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(userId);
    }
}