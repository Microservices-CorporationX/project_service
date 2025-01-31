package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.subprojectdto.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subprojectdto.ProjectReadDto;
import faang.school.projectservice.dto.client.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.filter.SubProjectNameFilter;
import faang.school.projectservice.filter.SubProjectStatusFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ValidatorProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final ValidatorProjectService validatorProjectService;

    public ProjectReadDto createSubProject(CreateSubProjectDto createSubProjectDto) {

        validatorProjectService.validateProjectExistence(createSubProjectDto.getParentProjectId());

        Project parentProject = projectRepository.findById(createSubProjectDto.getParentProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Родительский проект не найден"));

        validatorProjectService.validateVisibility(parentProject, createSubProjectDto.getVisibility());
        validatorProjectService.validateParentProjectStatus(parentProject);

        Project projectToSave = subProjectMapper.mapToEntity(createSubProjectDto);
        projectToSave.setParentProject(parentProject);

        Project savedProject = projectRepository.save(projectToSave);
        return subProjectMapper.mapToProjectDto(savedProject);
    }

    public ProjectReadDto updateSubProject(Long projectId, CreateSubProjectDto createSubProjectDto) {

        validatorProjectService.validateProjectExistence(createSubProjectDto.getParentProjectId());

        Project parentProject = projectRepository.findById(createSubProjectDto.getParentProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Родительский проект не найден"));

        validatorProjectService.validateParentProjectStatus(parentProject);
        validatorProjectService.validateVisibility(parentProject, createSubProjectDto.getVisibility());

        Project projectToUpdate = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект не найден"));

        projectToUpdate.setName(createSubProjectDto.getName());
        projectToUpdate.setDescription(createSubProjectDto.getDescription());
        projectToUpdate.setVisibility(createSubProjectDto.getVisibility());
        projectToUpdate.setStatus(createSubProjectDto.getStatus());

        Project updatedProject = projectRepository.save(projectToUpdate);
        return subProjectMapper.mapToProjectDto(updatedProject);
    }


    public List<ProjectReadDto> getFilteredSubProjects(String name, ProjectStatus status) {
        List<Project> projects = projectRepository.findAll();

        SubProjectFilterDto filterDto = new SubProjectFilterDto(name, status);


        SubProjectNameFilter nameFilter = new SubProjectNameFilter();
        List<Project> filteredByName = nameFilter.apply(projects, filterDto);

        SubProjectStatusFilter statusFilter = new SubProjectStatusFilter();
        List<Project> filteredByStatus = statusFilter.apply(filteredByName, filterDto);

        return filteredByStatus.stream()
                .map(subProjectMapper::mapToProjectDto)
                .collect(Collectors.toList());
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект не найден"));
    }
}
