package faang.school.projectservice.service.subprojectService;

import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectFilterDto.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.mapper.subprojectMapper.ProjectMapper;
import faang.school.projectservice.service.filters.SubprojectFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<SubprojectFilter> subprojectFilters;


    public ProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto createSubProjectDto) {

            Project parentProject = projectRepository.getProjectById(parentProjectId);

            Project project = projectMapper.toEntity(createSubProjectDto);
            project.setStatus(createSubProjectDto.getStatus());
            if (parentProject.getVisibility() == ProjectVisibility.PUBLIC && createSubProjectDto.getIsPrivate()) {
                throw new IllegalArgumentException("Private projects are not allowed");
            }
            project.setVisibility(createSubProjectDto.getVisibility());
            project.setParentProject(parentProject);
            Project savedProject = projectRepository.save(project);
            return projectMapper.toDto(savedProject);
    }

    public ProjectDto updateSubProject(Long subProjectId, CreateSubProjectDto updateSubProjectDto) {
        Project subProject = projectRepository.getProjectById(subProjectId);
        if (subProject == null) {
            throw new EntityNotFoundException("Subproject with ID " + subProjectId + " not found");
        }

        Project parentProject = subProject.getParentProject();

        if (parentProject != null && parentProject.getStatus() == ProjectStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update subproject because the parent project is already closed.");
        }

        subProject.setName(updateSubProjectDto.getName());
        subProject.setDescription(updateSubProjectDto.getDescription());
        subProject.setStatus(updateSubProjectDto.getStatus());
        subProject.setParentProject(parentProject);

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE){
            subProject.setVisibility(ProjectVisibility.PRIVATE);
        }
        else {
            subProject.setVisibility(updateSubProjectDto.getVisibility());
        }


        if (parentProject.getStatus() == ProjectStatus.COMPLETED){
            List<Project> children = parentProject.getChildren();
            boolean hasOpenSubProjects = children.stream().anyMatch(sub ->
                    sub.getStatus() == ProjectStatus.CREATED ||
                            sub.getStatus() == ProjectStatus.IN_PROGRESS ||
                            sub.getStatus() == ProjectStatus.ON_HOLD
            );

            if (hasOpenSubProjects) {
                throw new IllegalStateException("Cannot close parent project because there are open subprojects.");
            }
        }
        try {
            Project updatedSubProject = projectRepository.save(subProject);
            return projectMapper.toDto(updatedSubProject);
        }catch (Exception e){
            throw new RuntimeException("Failed to update subproject", e);
        }

    }


    public List<ProjectDto> getSubProject(Long parentProjectId, SubprojectFilterDto subprojectFilterDto) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);
        if (parentProject == null || parentProject.getChildren() == null) {
            return List.of();
        }
        Stream<Project> filteredProjects = parentProject.getChildren().stream();

        return subprojectFilters.stream().filter(filter -> filter.isApplicable(subprojectFilterDto))
                .flatMap(filter -> filter.apply(filteredProjects, subprojectFilterDto))
                .map(projectMapper::toDto)
                .toList();
    }
}
