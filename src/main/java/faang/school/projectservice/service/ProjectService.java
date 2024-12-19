package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.TaskDto;
import faang.school.projectservice.model.Team;
import java.util.List;

public interface ProjectService {
    ProjectDto getProjectById(Long projectId);

    void saveNewTeam(Team team, Long projectId);

    List<Long> getAllTeamMembersIds(Long projectId);

    List<TaskDto> getProjectTasks(Long projectId);

    void deleteTeamMember(Long projectId, Long teamMemberId);
}
