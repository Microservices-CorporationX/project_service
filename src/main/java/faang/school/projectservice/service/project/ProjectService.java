package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Optional<Project> findProject(Long id){
        if (id == null){
            throw new IllegalArgumentException("Project not found");
        }

        return Optional.ofNullable(projectRepository.getProjectById(id));
    }

    public Project updateProject(Project project){
        return projectRepository.save(project);
    }
}
