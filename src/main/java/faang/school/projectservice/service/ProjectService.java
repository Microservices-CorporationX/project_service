package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.UpdateProjectMapper;
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
    private final UpdateProjectMapper updateProjectMapper;

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
    public UpdateProjectDto updateProjectDescription(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());
        projectValidator.validateProjectDescriptionUpdatable(dto, project);

        project.setDescription(dto.getDescription());
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} description successfully updated.", updatedProject.getId());
        return updateProjectMapper.toDto(updatedProject);
    }

    @Transactional
    public UpdateProjectDto updateProjectStatus(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());
        projectValidator.validateProjectStatusUpdatable(dto, project);

        project.setStatus(dto.getStatus());
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} status successfully updated.", updatedProject.getId());
        return updateProjectMapper.toDto(updatedProject);
    }

    @Transactional
    public UpdateProjectDto updateProjectVisibility(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());
        projectValidator.validateProjectVisibilityUpdatable(dto, project);

        project.setVisibility(dto.getVisibility());
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} visibility successfully updated.", updatedProject.getId());
        return updateProjectMapper.toDto(updatedProject);
    }
}