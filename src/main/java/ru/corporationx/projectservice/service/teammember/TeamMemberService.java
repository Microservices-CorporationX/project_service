package ru.corporationx.projectservice.service.teammember;


import ru.corporationx.projectservice.model.entity.TeamMember;

public interface TeamMemberService {
    TeamMember findById(Long id);
    TeamMember validateUserIsProjectMember(long userId, long projectId);
}
