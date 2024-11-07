package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectDto createProject(ProjectDto dto) {
        projectValidator.validateUniqueProject(dto);
        Project project = projectMapper.toEntity(dto);
        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);
        log.info("Project #{} successfully created.", savedProject.getId());
        return projectMapper.toDto(savedProject);
    }
}