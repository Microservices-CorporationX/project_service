package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto create(CreateSubProjectDto createDto) {
        validateSubProjectCreation(createDto);

        Project subProject = projectMapper.toEntity(createDto);
        subProject = projectRepository.save(subProject);
        return projectMapper.toDto(subProject);
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
}
