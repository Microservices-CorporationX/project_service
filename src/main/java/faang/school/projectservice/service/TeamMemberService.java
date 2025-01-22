package faang.school.projectservice.service;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public void areTeamMembersExist(List<Long> userIds) {
        userIds.forEach(id -> {
            if (getTeamMembersByUserId(id).isEmpty()) {
                throw new EntityNotFoundException("Участника команды с userId=" + id + " не существует");
            }
        });
    }

    private List<TeamMember> getTeamMembersByUserId(Long userId) {
        return teamMemberRepository.findByUserId(userId);
    }
}
