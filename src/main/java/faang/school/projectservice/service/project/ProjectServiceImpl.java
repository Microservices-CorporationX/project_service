package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.TaskDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    @Override
    public ProjectDto getProjectById(Long projectId) {
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    @Override
    public void saveNewTeam(Team team, Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        project.getTeams().add(team);
        projectRepository.save(project);
    }

    @Override
    public List<Long> getAllTeamMembersIds(Long projectId) {
        return projectRepository.getProjectById(projectId).getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream().map(TeamMember::getId))
                .toList();
    }

    @Override
    public List<TaskDto> getProjectTasks(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        return project.getTasks().stream().map(taskMapper::toDto).toList();
    }

    @Override
    public void deleteTeamMember(Long projectId, Long teamMemberId) {
        Project project = projectRepository.getProjectById(projectId);
        List<Team> teamList = project.getTeams();
        Team team = teamList.stream().filter(teamItem -> teamItem.getTeamMembers().stream()
                .anyMatch(teamMember -> teamMember.getId().equals(teamMemberId))).findAny().orElseThrow(
                () -> new DataValidationException("Intern with id " + teamMemberId + "does not exists in project")
        );
        List<TeamMember> filteredTeamMembers = team.getTeamMembers().stream()
                .filter(teamMember -> !teamMember.getId().equals(teamMemberId))
                .toList();
        team.setTeamMembers(filteredTeamMembers);
        int index = teamList.indexOf(team);
        teamList.set(index, team);
        project.setTeams(teamList);
        projectRepository.save(project);
    }
}
