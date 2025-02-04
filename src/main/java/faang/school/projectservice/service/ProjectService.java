package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.FilterProjects;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final SubProjectValidator subProjectValidator;
    private final ProjectValidator projectValidator;
    private final List<FilterProjects> projectFilters;

    public Project createSubProject(Long parentId, Project subProject) {
        Optional<Project> parentProject = projectRepository.findById(parentId);
        projectValidator.doesProjectExist(parentProject);
        subProjectValidator.canBeParentProject(parentProject.get());

        return projectRepository.save(mappingNewEntity(subProject, parentProject.get()));
    }

    private Project mappingNewEntity(Project subProject, Project parentProject) {
        Project newProject = new Project();
        newProject.setName(subProject.getName());
        newProject.setDescription(subProject.getDescription());
        newProject.setOwnerId(subProject.getOwnerId());
        newProject.setParentProject(parentProject);
        newProject.setVisibility(parentProject.getVisibility());
        newProject.setStatus(ProjectStatus.CREATED);
        newProject.setCreatedAt(LocalDateTime.now());
        newProject.setChildren(List.of());
        return newProject;
    }


    public Project updateSubProject(Long id, ProjectStatus status, ProjectVisibility visibility) {
        Optional<Project> project = projectRepository.findById(id);
        projectValidator.doesProjectExist(project);

        Project updateProject = project.get();
        updateProject.setVisibility(visibility);
        setUpChildVisibility(updateProject, visibility);

        if (status.equals(ProjectStatus.COMPLETED)) {
            subProjectValidator.childCompleted(updateProject.getChildren());
            //momentService.createMoment(id, "name", updateProject.getChildren())
        }
        updateProject.setStatus(status);
        updateProject.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(updateProject);
    }

    private void setUpChildVisibility(Project project, ProjectVisibility visibility) {
        List<Project> subProjects = project.getChildren();
        if (subProjects != null) {
            subProjects.forEach(subP -> subP.setVisibility(visibility));
        }
    }


    public List<Project> getSubProjects(Long id, FilterSubProjectDto filters, Integer limitList) {
        Optional<Project> parentProject = projectRepository.findById(id);
        projectValidator.doesProjectExist(parentProject);
        subProjectValidator.shouldBePublic(parentProject.get());

        Stream<Project> subProjects = parentProject.get().getChildren().stream();
        return projectFilters.stream()
                .filter(filtr -> filtr.isApplicable(filters))
                .flatMap(filtr -> filtr.apply(subProjects, filters))
                .limit(limitList)
                .toList();
    }
}
