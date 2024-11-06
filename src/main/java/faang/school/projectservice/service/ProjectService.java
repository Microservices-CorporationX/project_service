package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(Long ownerId, ProjectDto dto) {
        projectValidator.validateUniqueProject(dto.getName(), ownerId);
        Project project = projectMapper.toEntity(dto);
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(projectRepository.save(project));
    }
}