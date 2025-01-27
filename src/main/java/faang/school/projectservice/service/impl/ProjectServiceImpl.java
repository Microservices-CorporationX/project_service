package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDto getProject(Long projectId) {
        Project project = getProjectById(projectId);
        return projectMapper.toProjectResponseDto(project);
    }

    public List<Project> getProjectsByIds (List<Long> projectIds) {
        return projectRepository.findAllById(projectIds);
    }

    private Project getProjectById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        return optionalProject.orElse(new Project());
    }
}
