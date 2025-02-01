package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public boolean isUserInProject(Long userId, Long projectId) {
        Project project = getProject(projectId);
        List<Long> projectUserIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .sorted()
                .toList();
        return projectUserIds.contains(userId);
    }

    @Override
    public boolean isProjectPublic(Long projectId) {
        Project project = getProject(projectId);
        return ProjectVisibility.PUBLIC.equals(project.getVisibility());
    }

    @Override
    public List<Long> getProjectResourceIds(Long projectId) {
        Project project = getProject(projectId);
        return project.getResources().stream()
                .map(Resource::getId)
                .sorted()
                .toList();
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow();
    }

}
