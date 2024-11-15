package faang.school.projectservice.subproject;

import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.mapper.subprojectMapper.ProjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubProjectService {

    private  ProjectRepository projectRepository;
    private  ProjectMapper projectMapper;


    public ProjectDto createSubProject(Long parentProjectId, CreateSubProjectDto createSubProjectDto) {
            Project parentProject = projectRepository.getProjectById(parentProjectId);

            Project project = projectMapper.toEntity(createSubProjectDto);

            if (parentProject.getVisibility() == ProjectVisibility.PUBLIC && createSubProjectDto.getIsPrivate()) {
                throw new IllegalArgumentException("Private projects are not allowed");
            }

            project.setParentProject(parentProject);
            Project savedProject = projectRepository.save(project);
            return projectMapper.toDto(savedProject);


    }

    public ProjectDto updateSubProject(Long subProjectId, CreateSubProjectDto updateSubProjectDto) {
        Project subProject = projectRepository.getProjectById(subProjectId);
        if (subProject == null) {
            throw new EntityNotFoundException(String.format("Subproject with ID %d not found", subProjectId));
        }
        Project parentProject = subProject.getParentProject();
        if (parentProject != null && parentProject.getStatus() == ProjectStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update subproject because the parent project is already closed.");
        }

        subProject.setName(updateSubProjectDto.getName());
        subProject.setDescription(updateSubProjectDto.getDescription());
        subProject.setStatus(updateSubProjectDto.getStatus());

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


            Project updatedSubProject = projectRepository.save(subProject);

            return projectMapper.toDto(updatedSubProject);

    }


    public ProjectDto getSubProject(Long parentProjectId, Long subProjectId) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);

        if (parentProject == null) {
            throw new EntityNotFoundException("Parent project with ID " + parentProjectId + " not found.");
        }

        boolean isSubProjectVisible = parentProject.getChildren().stream()
                .anyMatch(subProject -> subProject.getVisibility() == ProjectVisibility.PRIVATE);

        if (isSubProjectVisible) {
            throw new EntityNotFoundException("Cannot find subproject because it is private.");
        }

        if (parentProject.getChildren().isEmpty()) {
            throw new EntityNotFoundException("Parent project with ID " + parentProjectId + " has no subprojects.");
        }

        return parentProject.getChildren().stream()
                .filter(project -> project.getId().equals(subProjectId))
                .findFirst()
                .map(projectMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Subproject with ID " + subProjectId + " not found"));
    }

}
