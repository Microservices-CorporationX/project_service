package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.validator.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentRepository momentRepository;
    private final SubProjectValidator projectValidator;

    public ProjectDto create(CreateSubProjectDto createDto) {
        projectValidator.validateSubProjectCreation(createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject = projectRepository.save(subProject);
        return projectMapper.toDto(subProject);
    }

    public ProjectDto update(UpdateSubProjectDto updateDto) {
        Project project = getProjectById(updateDto.getId());
        projectMapper.updateEntityFromDto(updateDto, project);
        List<Project> subProjects = project.getChildren();

        projectValidator.validateSubProjectStatuses(subProjects, project.getStatus());
        applyPrivateVisibilityIfParentIsPrivate(subProjects, updateDto.getVisibility());

        if (isAllSubProjectsCompleted(subProjects)) {
            addMomentToProject(project);
        }

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getSubProjects(long projectId) {
        Project project = getProjectById(projectId);
        List<Project> subProjects = project.getChildren();

        List<Project> filteredSubProjects = subProjects.stream()
                .filter(subProject -> subProject.getVisibility() == ProjectVisibility.PUBLIC)
                .sorted(Comparator.comparingInt((Project subProject) -> subProject.getStatus().ordinal())
                        .thenComparing(Project::getName))
                .toList();

        return filteredSubProjects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект с ID "
                        + projectId + " не найден"));
    }

    private Moment getMomentByName(String name) {
        Moment probe = new Moment();
        probe.setName(name);
        Example<Moment> example = Example.of(probe);
        return momentRepository.findOne(example)
                .orElseThrow(() -> new EntityNotFoundException("Момент c названием '" + name + "' не найден"));
    }

    private void addMomentToProject(Project project) {
        List<Moment> moments = project.getMoments();
        moments.add(getMomentByName("Выполнены все подпроекты"));
        project.setMoments(moments);
    }

    private void applyPrivateVisibilityIfParentIsPrivate(List<Project> subProjects, ProjectVisibility parentVisibility) {
        if (parentVisibility == ProjectVisibility.PRIVATE) {
            subProjects.forEach(subProject -> subProject.setVisibility(ProjectVisibility.PRIVATE));
        }
    }

    private boolean isAllSubProjectsCompleted(List<Project> subProjects) {
        return subProjects.stream()
                .allMatch(subProject -> subProject.getStatus() == ProjectStatus.COMPLETED);
    }
}
