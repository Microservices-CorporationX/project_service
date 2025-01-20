package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateSubProjectCreation(CreateSubProjectDto createDto) {
        Project parentProject = projectRepository.findById(createDto.getParentProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Проект с ID "
                        + createDto.getParentProjectId() + " не найден"));

        if (parentProject.getParentProject() != null) {
            throw new BusinessException("Корневой проект не может иметь родительского проекта");
        }

        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && createDto.getVisibility() == ProjectVisibility.PUBLIC) {
            throw new BusinessException("Нельзя создать публичный подпроект для приватного родительского проекта");
        }
    }

    public void validateSubProjectStatuses(List<Project> subProjects, ProjectStatus parentStatus) {
        subProjects.forEach(subProject -> {
            if (subProject.getStatus() != parentStatus) {
                throw new BusinessException("Все подпроекты текущего подпроекта должны иметь одинаковый статус.");
            }
        });
    }
}
