package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private TeamMemberService teamMemberService;

    @BeforeEach
    void setUp() {
        teamMemberService = new TeamMemberService(teamMemberRepository);
    }

    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final List<Long> userIds = List.of(userId1, userId2);
    private final List<TeamMember> teamMembers = List.of(TeamMember.builder().build());

    @Test
    public void shouldSuccessWhenTeamMembersAreExits() {
        for (Long userId : userIds) {
            when(teamMemberRepository.findByUserId(userId)).thenReturn(teamMembers);
        }

        teamMemberService.areTeamMembersExist(userIds);

        for (Long userId : userIds) {
            verify(teamMemberRepository).findByUserId(userId);
        }
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenTeamMembersAreNotExits() {
        when(teamMemberRepository.findByUserId(userId1)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> teamMemberService.areTeamMembersExist(userIds));

        verify(teamMemberRepository).findByUserId(userId1);
        verify(teamMemberRepository, times(0)).findByUserId(userId2);
    }
}