package faang.school.projectservice.service.impl;


import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static faang.school.projectservice.constant.ProjectErrorMessages.PROJECT_WITH_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto getProject(long projectId) {
        return projectRepository.findById(projectId)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PROJECT_WITH_ID_NOT_FOUND, projectId)));

    }
}
