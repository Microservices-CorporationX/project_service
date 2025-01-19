package faang.school.projectservice.service;


import faang.school.projectservice.dto.Project.ProjectDto;
import faang.school.projectservice.dto.Project.ProjectFilterDto;
import faang.school.projectservice.dto.Project.ProjectUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.projectfilter.ProjectNameFilter;
import faang.school.projectservice.filter.projectfilter.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectNameFilter projectNameFilter;
    private final ProjectStatusFilter projectStatusFilter;


    public ProjectDto createProject(ProjectDto dto) {
        projectValidator.validateUniqueProject(dto);
        Project project = projectMapper.toEntity(dto);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);
        log.info("Project #{} successfully created.", savedProject.getId());

        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectDto updateProject(ProjectUpdateDto dto) {

        Project project = findProjectById(dto.getId());
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
        if (dto.getVisibility() != null) {
            project.setVisibility(dto.getVisibility());
        }
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} successfully updated. Current data: description: '{}'; status: '{}'; visibility: '{}'",
                updatedProject.getId(), updatedProject.getDescription(),
                updatedProject.getStatus(), updatedProject.getVisibility());
        return projectMapper.toDto(updatedProject);
    }

    public Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public List<ProjectDto> getProjectsByFilterName(ProjectFilterDto filterDto, Long currentUserId) {
        List<Project> projects = getAllUserAvailableProjects(currentUserId);

        List<ProjectDto> result = projects.stream()
                .filter(project -> projectNameFilter.isApplicable(filterDto))
                .flatMap(project -> projectNameFilter.apply(projects.stream(), filterDto))
                .map(projectMapper::toDto)
                .toList();

        log.info("Projects filtered by {}.", filterDto);
        return result;

    }

    public List<ProjectDto> getProjectsByFilterStatus(ProjectFilterDto filterDto, Long currentUserId) {
        List<Project> projects = getAllUserAvailableProjects(currentUserId);

        List<ProjectDto> result = projects.stream()
                .filter(project -> projectStatusFilter.isApplicable(filterDto))
                .flatMap(project -> projectStatusFilter.apply(projects.stream(), filterDto))
                .map(projectMapper::toDto)
                .toList();

        log.info("Projects filtered by {}.", filterDto);
        return result;

    }

    public List<Project> getAllUserAvailableProjects(Long currentUserId) {

        return projectRepository.findAll()
                .stream()
                .filter(project -> projectValidator.canUserAccessProject(project, currentUserId))
                .toList();
    }
}