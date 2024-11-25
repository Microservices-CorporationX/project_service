package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        teamMember = createTestTeamMember();
    }

    @Test
    @DisplayName("Get team member by id")
    void testGetTeamMemberById() {
        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(teamMember);

        TeamMember result = teamMemberService.getTeamMemberByUserId(teamMember.getId());

        assertNotNull(result);
        assertEquals(teamMember, result);
        assertEquals(TeamRole.OWNER, result.getRoles().get(0));
    }

    private TeamMember createTestTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }


    @Test
    void testValidateInvitedUsersExistInTeam_UsersExist() {
        MeetDto createMeetDto = MeetDto.builder()
                .participants(Arrays.asList(1L, 2L, 3L))
                .build();

        when(teamMemberRepository.existsById(1L)).thenReturn(true);
        when(teamMemberRepository.existsById(2L)).thenReturn(true);
        when(teamMemberRepository.existsById(3L)).thenReturn(true);

        teamMemberService.validateInvitedUsersExistInTeam(createMeetDto);

        verify(teamMemberRepository, times(3)).existsById(anyLong());
    }

    @Test
    void testValidateInvitedUsersExistInTeam_UserDoesNotExist() {
        MeetDto createMeetDto = MeetDto.builder()
                .participants(Arrays.asList(1L, 2L, 3L))
                .build();

        when(teamMemberRepository.existsById(1L)).thenReturn(true);
        when(teamMemberRepository.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> teamMemberService.validateInvitedUsersExistInTeam(createMeetDto));
        verify(teamMemberRepository, times(2)).existsById(anyLong());
    }

    @Test
    void testValidateUserExistsInTeam_UserExists() {
        Long user1 = 1L;
        Long user2 = 2L;
        Long user3 = 3L;

        MeetDto meetDto = MeetDto.builder()
                .creatorId(user2)
                .participants(Arrays.asList(user1, user2, user3))
                .build();

        when(teamMemberRepository.existsById(user2)).thenReturn(true);
        when(teamMemberRepository.existsById(user1)).thenReturn(true);
        when(teamMemberRepository.existsById(user3)).thenReturn(true);

        teamMemberService.validateInvitedUsersExistInTeam(meetDto);

        verify(teamMemberRepository, times(1)).existsById(user2);
        verify(teamMemberRepository, times(1)).existsById(user1);
        verify(teamMemberRepository, times(1)).existsById(user3);
    }

    @Test
    void testValidateUserExistsInTeam_UserDoesNotExist() {
        MeetDto meetDto = MeetDto.builder()
                .creatorId(1L)
                .participants(Arrays.asList(1L, 2L, 3L))
                .build();

        when(teamMemberRepository.existsById(meetDto.getCreatorId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> teamMemberService.validateInvitedUsersExistInTeam(meetDto));

        verify(teamMemberRepository, times(1)).existsById(meetDto.getCreatorId());
    }
}
