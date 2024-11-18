package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberById(long id) {
        return teamMemberRepository.findById(id);
    }

    public void setTeamMembersRoleNonIntern(List<Long> teamMemberIds) {
        teamMemberRepository.findByIds(teamMemberIds)
                .forEach(intern -> removeTeamRole(intern, TeamRole.INTERN));
    }

    public void removeTeamRole(TeamMember teamMember, TeamRole teamRole) {
        teamMember.getRoles().remove(teamRole);
    }
}
