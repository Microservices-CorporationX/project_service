package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.projectfilter.ProjectNameFilter;
import faang.school.projectservice.service.projectfilter.ProjectStatusFilter;
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
    private final ProjectNameFilter projectNameFilter;
    private final ProjectStatusFilter projectStatusFilter;

    public Project createProject(Project project) {
        projectValidator.validateUniqueProject(project);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        Project savedProject = projectRepository.save(project);
        log.info("Project #{} successfully created.", savedProject.getId());

        return savedProject;
    }

    @Transactional
    public Project updateProject(Project project) {

        Project projectToUpdate = findProjectById(project.getId());
        if (project.getDescription() != null && !project.getDescription().isBlank()) {
            projectToUpdate.setDescription(project.getDescription());
        }
        if (project.getStatus() != null) {
            projectToUpdate.setStatus(project.getStatus());
        }
        if (project.getVisibility() != null) {
            projectToUpdate.setVisibility(project.getVisibility());
        }
        projectToUpdate.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(projectToUpdate);

        log.info("Project #{} successfully updated. Current data: description: '{}'; status: '{}'; visibility: '{}'",
                updatedProject.getId(), updatedProject.getDescription(),
                updatedProject.getStatus(), updatedProject.getVisibility());
        return updatedProject;
    }

    public Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public List<Project> getProjectsByFilterName(ProjectFilterDto filterDto, Long currentUserId) {
        List<Project> projects = getAllUserAvailableProjects(currentUserId);

        List<Project> result = projects.stream()
                .filter(project -> projectNameFilter.isApplicable(filterDto))
                .flatMap(project -> projectNameFilter.apply(projects.stream(), filterDto))
                .toList();

        log.info("Projects filtered by {}.", filterDto);
        return result;

    }

    public List<Project> getProjectsByFilterStatus(ProjectFilterDto filterDto, Long currentUserId) {
        List<Project> projects = getAllUserAvailableProjects(currentUserId);

        List<Project> result = projects.stream()
                .filter(project -> projectStatusFilter.isApplicable(filterDto))
                .flatMap(project -> projectStatusFilter.apply(projects.stream(), filterDto))
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
