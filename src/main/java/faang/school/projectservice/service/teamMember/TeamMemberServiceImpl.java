package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.dto.internship.RoleDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.TeamMemberService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id);
    }

    @Override
    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    @Override
    public void addNewRole(Long teamMemberId, RoleDto role) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        List<TeamRole> memberRoles = new ArrayList<>(teamMember.getRoles());
        memberRoles.add(role.toTeamRole());
        teamMember.setRoles(memberRoles);
        teamMemberRepository.save(teamMember);
    }
}
