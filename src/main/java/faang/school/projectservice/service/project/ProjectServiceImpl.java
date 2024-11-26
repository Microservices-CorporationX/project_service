package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    @Override
    public void saveNewTeam(Team team, Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        project.getTeams().add(team);
        projectRepository.save(project);
    }

    @Override
    public void saveProject(Project project) {
        projectRepository.save(project);
    }
}
