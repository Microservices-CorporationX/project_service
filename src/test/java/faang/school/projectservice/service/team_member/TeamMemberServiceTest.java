package faang.school.projectservice.service.team_member;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {
    @InjectMocks
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        teamMember = TeamMember
                .builder()
                .userId(1L)
                .build();
    }

    @Test
    void testGetTeamMemberByUserIdWhenUserExists() {
        Long userId = 1L;

        when(teamMemberRepository.findById(userId)).thenReturn(teamMember);

        TeamMember foundMember = teamMemberService.getTeamMemberByUserId(userId);

        assertNotNull(foundMember, "Team member should not be null");
        assertEquals(teamMember.getId(), foundMember.getId(), "Team member ID should match");

        verify(teamMemberRepository, times(1)).findById(userId);
    }

    @Test
    void testGetTeamMemberByUserIdWhenUserDoesNotExist() {
        Long userId = 2L;

        when(teamMemberRepository.findById(userId)).thenReturn(null);

        TeamMember foundMember = teamMemberService.getTeamMemberByUserId(userId);

        assertNull(foundMember, "Team member should be null when user does not exist");

        verify(teamMemberRepository, times(1)).findById(userId);
    }

    @Test
    void existsByIdShouldReturnTrueWhenUserExists() {
        Long userId = 1L;
        when(teamMemberRepository.existsById(userId)).thenReturn(true);

        assertTrue(teamMemberService.existsById(userId));
    }

    @Test
    void existsByIdShouldReturnFalseWhenUserDoesNotExist() {
        Long userId = 2L;
        when(teamMemberRepository.existsById(userId)).thenReturn(false);

        assertFalse(teamMemberService.existsById(userId));
    }
}