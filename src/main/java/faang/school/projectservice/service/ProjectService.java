package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public List<Project> findAllById (List<Long> ids){
        return   projectRepository.findAllByIds(ids);
    }
}
