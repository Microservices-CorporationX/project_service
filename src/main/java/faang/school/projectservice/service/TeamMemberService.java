package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberByUserId(Long userId) {
        return teamMemberRepository.findById(userId);
    }

    public boolean existsById(Long userId) {
        return teamMemberRepository.existsById(userId);
    }

    public void validateInvitedUsersExistInTeam(MeetDto createMeetDto) {
        List<Long> invitedUsers = createMeetDto.getParticipants();
        for (Long invitedUserId : invitedUsers) {
            validateUserExistsInTeam(invitedUserId);
        }
    }

    private void validateUserExistsInTeam(Long userId) {
        if (!teamMemberRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found " + userId);
        }
    }
}
