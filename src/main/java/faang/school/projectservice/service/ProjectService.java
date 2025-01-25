package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.retriever.project.create_request.ProjectCreateRetriever;
import faang.school.projectservice.retriever.project.request.ProjectRetriever;
import faang.school.projectservice.retriever.project.update_request.ProjectUpdateRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    private final List<ProjectCreateRetriever> projectCreateRetrievers;
    private final List<ProjectUpdateRetriever> projectUpdateRetrievers;
    private final List<ProjectRetriever> projectRetrievers;

    @Transactional
    public ProjectResponseDto createProject(ProjectCreateRequestDto projectCreateRequestDto) {
        Project project = projectMapper.toProject(projectCreateRequestDto);

        retrieveDataByUniversalRetrievers(project, projectCreateRequestDto);
        projectCreateRetrievers.forEach(projectRetriever ->
                                projectRetriever.retrieveData(project, projectCreateRequestDto));

        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDto(savedProject);
    }

    @Transactional
    public ProjectResponseDto updateProject(ProjectUpdateRequestDto projectUpdateRequestDto) {
        Project project = projectRepository.findById(projectUpdateRequestDto.getId())
                .orElseThrow(NoSuchElementException::new);
        projectMapper.update(project, projectUpdateRequestDto);

        retrieveDataByUniversalRetrievers(project, projectUpdateRequestDto);
        projectUpdateRetrievers.forEach(projectRetriever ->
                projectRetriever.retrieveData(project, projectUpdateRequestDto));

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDto(savedProject);
    }

    public List<ProjectResponseDto> getAllProjects(ProjectFilterDto filters) {
        Stream<Project> projectStream = projectRepository.findAll()
                .stream();
        if (filters != null && projectFilters != null && !projectFilters.isEmpty()) {
            for (ProjectFilter projectFilter : projectFilters) {
                if (projectFilter.isApplicable(filters)) {
                    projectStream = projectFilter.apply(projectStream, filters);
                }
            }
        }
        return projectStream.map(projectMapper::toResponseDto)
                .toList();
    }

    public ProjectResponseDto getProjectDtoById(Long id) {
        Project project = getProjectById(id);
        return projectMapper.toResponseDto(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> getProjectsByIds(List<Long> projectIds) {
        return projectRepository.findAllById(projectIds);
    }

    private void retrieveDataByUniversalRetrievers(Project project, ProjectRequestDto projectRequestDto) {
        projectRetrievers.forEach(projectRetriever ->
                projectRetriever.retrieveData(project, projectRequestDto));
    }
}
