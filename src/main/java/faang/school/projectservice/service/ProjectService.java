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

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @Transactional
    public ProjectDto updateProjectDescription(ProjectDto dto) {
        projectValidator.validateProjectExists(dto);
        projectValidator.validateProjectDescriptionUpdatable(dto);

        Project project = projectRepository.getProjectById(dto.getId());
        project.setDescription(dto.getDescription());
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} description successfully updated.", updatedProject.getId());
        return projectMapper.toDto(updatedProject);
    }

    @Transactional
    public ProjectDto updateProjectStatus(ProjectDto dto) {
        projectValidator.validateProjectExists(dto);
        projectValidator.validateProjectStatusUpdatable(dto);

        Project project = projectRepository.getProjectById(dto.getId());
        project.setStatus(dto.getStatus());
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} status successfully updated.", updatedProject.getId());
        return projectMapper.toDto(updatedProject);
    }

    @Transactional
    public ProjectDto updateProjectVisibility(ProjectDto dto) {
        projectValidator.validateProjectExists(dto);
        projectValidator.validateProjectVisibilityUpdatable(dto);

        Project project = projectRepository.getProjectById(dto.getId());
        project.setVisibility(dto.getVisibility());
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} visibility successfully updated.", updatedProject.getId());
        return projectMapper.toDto(updatedProject);
    }
}