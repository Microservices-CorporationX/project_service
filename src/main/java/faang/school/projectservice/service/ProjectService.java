package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final static String PROJECT = "Project";

    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectJpaRepository projectRepository;

    @Transactional(readOnly = true)
    public ProjectDto findById(long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(PROJECT, projectId));
        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjects(ProjectFilterDto filters, Long userId) {
        return projectRepository.findAll().stream()
                .filter(project -> isProjectVisibleForUser(project, userId))
                .flatMap(project -> applyFilters(project, filters))
                .distinct()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto, Long userId) {
        if (projectRepository.existsByOwnerIdAndName(userId, projectDto.getName())) {
            throw new AlreadyExistsException(PROJECT);
        }

        Project project = projectMapper.toEntityCreate(projectDto);
        project.setOwnerId(userId);

        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(PROJECT, projectDto.getId()));

        projectMapper.toEntityUpdate(projectDto);
        Project savedProject = projectRepository.save(existingProject);

        return projectMapper.toDto(savedProject);
    }

    private boolean isProjectVisibleForUser(Project project, Long userId) {
        return project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(userId);
    }

    private Stream<Project> applyFilters(Project project, ProjectFilterDto filters) {
        Stream<Project> projectStream = Stream.of(project);
        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(filters)) {
                projectStream = filter.apply(projectStream, filters);
            }
        }
        return projectStream;
    }
}
