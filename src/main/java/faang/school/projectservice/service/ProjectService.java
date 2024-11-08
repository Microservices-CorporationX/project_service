package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.UpdateProjectMapper;
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
    public UpdateProjectDto updateProject(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());

        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
        if (dto.getVisibility() != null) {
            project.setVisibility(dto.getVisibility());
        }

        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} successfully updated. Current data: description: '{}'; status: '{}'; visibility: '{}'",
                updatedProject.getId(), updatedProject.getDescription(),
                updatedProject.getStatus(), updatedProject.getVisibility());
        return updateProjectMapper.toDto(updatedProject);
    }
}