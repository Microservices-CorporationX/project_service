package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.SubProjectCreateDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentRepository momentRepository;
    private final ProjectValidator projectValidator;
    private final List<SubProjectFilter> subProjectFilters;

    public ProjectReadDto create(SubProjectCreateDto createDto) {
        projectValidator.validateSubProjectCreation(createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject = projectRepository.save(subProject);
        return projectMapper.toDto(subProject);
    }

    public ProjectReadDto update(SubProjectUpdateDto updateDto) {
        Project project = getProjectById(updateDto.getId());
        projectMapper.updateEntityFromDto(updateDto, project);
        List<Project> subProjects = project.getChildren();

        projectValidator.validateSubProjectStatuses(subProjects, project.getStatus());
        projectValidator.applyPrivateVisibilityIfParentIsPrivate(subProjects, updateDto.getVisibility());

        if (projectValidator.isAllSubProjectsCompleted(subProjects)) {
            addMomentToProject(project);
        }

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectReadDto> getSubProjects(long projectId, SubProjectFilterDto filterDto) {
        Project project = getProjectById(projectId);
        List<Project> subProjects = project.getChildren();

        return subProjects.stream()
                .filter(subProject -> subProjectFilters.stream().filter(filter -> filter.isApplicable(filterDto))
                        .anyMatch(filter -> filter.filterEntity(subProject, filterDto)))
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

}
