package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import java.util.List;

public interface TeamService {
    Team createTeam(List<TeamMember> members, Project project);
}
