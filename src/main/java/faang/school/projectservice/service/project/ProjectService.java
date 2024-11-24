package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final static String PROJECT = "Project";

    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectJpaRepository projectRepository;

    @Transactional(readOnly = true)
    public ProjectDto findById(long projectId) {
        Project project = getProjectById(projectId);
        log.info("Project found with ID: {}", projectId);
        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjects(ProjectFilterDto filters, Long userId) {
        List<ProjectDto> projects = projectRepository.findAll().stream()
                .filter(project -> isProjectVisibleForUser(project, userId))
                .flatMap(project -> applyFilters(project, filters))
                .distinct()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
        log.info("Projects found: {}", projects.size());
        return projects;
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto, Long userId) {
        validateProjectExistsForUser(userId, projectDto.getName());
        Project project = projectMapper.toEntityCreate(projectDto);
        project.setOwnerId(userId);
        Project savedProject = projectRepository.save(project);
        log.info("Project created with ID: {}", savedProject.getId());
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(PROJECT, projectDto.getId()));

        projectMapper.toEntityUpdate(projectDto, existingProject);
        Project savedProject = projectRepository.save(existingProject);
        log.info("Project updated with ID: {}", savedProject.getId());
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public List<Project> findProjectsByIds(List<Long> projectIds) {
        return projectIds.stream()
                .map(id -> projectRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(PROJECT, id)))
                .collect(Collectors.toList());
    }


    public boolean isProjectExists(long projectId) {
        return projectRepository.existsById(projectId);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(PROJECT, projectId));
    }

    private boolean isProjectVisibleForUser(Project project, Long userId) {
        return project.getVisibility() == ProjectVisibility.PUBLIC
                || project.getOwnerId().equals(userId);
    }

    private void validateProjectExistsForUser (Long userId, String name){
        if (projectRepository.existsByOwnerIdAndName(userId, name)) {
            throw new AlreadyExistsException(PROJECT);
        }
    }

    private Stream<Project> applyFilters (Project project, ProjectFilterDto filters){
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(Stream.of(project),
                        (stream, filter) -> filter.apply(stream, filters),
                        Stream::concat);
    }
}