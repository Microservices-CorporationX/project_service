package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
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

    public ProjectDto create(CreateSubProjectDto createDto) {
        validateSubProjectCreation(createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject = projectRepository.save(subProject);
        return projectMapper.toDto(subProject);
    }

    public ProjectDto update(UpdateSubProjectDto updateDto) {
        Project project = getProjectById(updateDto.getId());
        List<Project> subProjects = project.getChildren();

        validateSubProjectStatuses(subProjects, project.getStatus());
        applyPrivateVisibilityIfParentIsPrivate(subProjects, updateDto.getProjectVisibility());

        if (isAllSubProjectsCompleted(subProjects)) {
            List<Moment> moments = project.getMoments();
            moments.add(getMomentByName("Выполнены все подпроекты"));
            project.setMoments(moments);
        }

        Project updatedProject = projectMapper.toUpdatedEntity(updateDto);
        updatedProject = projectRepository.save(updatedProject);
        return projectMapper.toDto(updatedProject);
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


    private void validateSubProjectCreation(CreateSubProjectDto createDto) {
        Project parentProject = projectRepository.findById(createDto.getParentProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Проект с ID "
                        + createDto.getParentProjectId() + " не найден"));

        if (parentProject.getParentProject() != null) {
            throw new BusinessException("Корневой проект не может иметь родительского проекта");
        }

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && createDto.getProjectVisibility() == ProjectVisibility.PUBLIC) {
            throw new BusinessException("Нельзя создать публичный подпроект для приватного родительского проекта");
        }
    }

    private void validateSubProjectStatuses(List<Project> subProjects, ProjectStatus parentStatus) {
        subProjects.forEach(subProject -> {
            if (subProject.getStatus() != parentStatus) {
                throw new BusinessException("Все подпроекты текущего подпроекта должны иметь одинаковый статус.");
            }
        });
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
