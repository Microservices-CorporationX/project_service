package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;

public interface TeamMemberService {
    TeamMember findById(Long id);

    TeamMember save(TeamMember teamMember);
}
