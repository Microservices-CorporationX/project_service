package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final List<Filter<Project, ProjectFilterDto>> projectFilters;

    public ProjectDto getById(Long projectId) {
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public ProjectResponseDto createSubProject(Long parentId, CreateProjectDto createProjectDto) {

        return null;
    }

    public ProjectResponseDto updateSubProject(Long parentId, UpdateSubProjectDto updateSubProjectDto) {

        return null;
    }

    public List<ProjectResponseDto> filterSubProjects(Long parentId, ProjectFilterDto filters) {

        Project project = projectRepository.getProjectById(parentId);
        Stream<Project> childrenProjectsStream;

        if (projectValidator.isProjectPublic(project)) {
            childrenProjectsStream = project
                    .getChildren()
                    .stream()
                    .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC);
        } else {
            childrenProjectsStream = Stream.empty();
        }

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(childrenProjectsStream, filters))
                .map(projectMapper::toResponseDto)
                .toList();
    }
}
