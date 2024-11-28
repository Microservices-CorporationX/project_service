package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.RoleDto;
import faang.school.projectservice.model.TeamMember;

public interface TeamMemberService {
    TeamMember findById(Long id);

    TeamMember save(TeamMember teamMember);

    void addNewRole(Long teamMemberId, RoleDto role);
}
