package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberById(long id) {
        return teamMemberRepository.findById(id);
    }

    public List<TeamMember> getAllTeamMembersByIds(List<Long> ids) {
        return teamMemberRepository.findAllByIds(ids);
    }

    public void setTeamMembersRoleAndRemoveInternRole(List<Long> teamMemberIds, TeamRole teamRole) {
        List<TeamMember> teamMembers = teamMemberRepository.findAllByIds(teamMemberIds);
        teamMembers.forEach(intern -> {
            if (intern.getRoles() == null) {
                intern.setRoles(new ArrayList<>());
            }
            List<TeamRole> mutableRoles = new ArrayList<>(intern.getRoles());
            mutableRoles.add(teamRole);
            mutableRoles.remove(TeamRole.INTERN);
            intern.setRoles(mutableRoles);
        });

        teamMemberRepository.saveAll(teamMembers);
    }

    public void removeTeamRole(TeamMember teamMember, TeamRole teamRole) {
        List<TeamRole> mutableRoles = new ArrayList<>(teamMember.getRoles());
        mutableRoles.remove(teamRole);
        teamMember.setRoles(mutableRoles);

        teamMemberRepository.save(teamMember);
    }
}
