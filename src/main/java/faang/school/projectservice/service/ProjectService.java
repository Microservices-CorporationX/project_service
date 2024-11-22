package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;

public interface ProjectService {
    Project getProjectById(Long projectId);

    void saveNewTeam(Team team, Long projectId);

    void saveProject(Project project);
}
