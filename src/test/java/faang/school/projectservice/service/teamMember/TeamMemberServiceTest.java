package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.teammember.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    private static final String TEAM_MEMBER = "TeamMember";

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Test
    public void findByIdNotFoundTest() {
        Long userId = 1L;
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> teamMemberService.findById(userId));

        assertEquals("Entity %s with ID %s not found".formatted(TEAM_MEMBER, userId), exception.getMessage());
        verify(teamMemberRepository, times(1)).findById(userId);
    }

    @Test
    public void findByIdFoundTest() {
        Long userId = 1L;
        TeamMember teamMember = new TeamMember();
        teamMember.setId(userId);
        teamMember.setUserId(userId);
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.of(teamMember));

        TeamMember findMember = teamMemberService.findById(userId);

        assertEquals(teamMember.getId(), findMember.getId());
        assertEquals(teamMember.getUserId(), findMember.getUserId());
    }

    @Test
    void saveTeamMemberTest() {
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.save(teamMember)).thenReturn(teamMember);

        TeamMember savedTeamMember = assertDoesNotThrow(() -> teamMemberService.save(teamMember));
        verify(teamMemberRepository, times(1)).save(teamMember);
        assertEquals(teamMember, savedTeamMember);
    }

    @Test
    void saveTeamMembersTest() {
        TeamMember teamMember = new TeamMember();
        List<TeamMember> membersToSave = List.of(teamMember);
        when(teamMemberRepository.saveAll(membersToSave)).thenReturn(membersToSave);

        List<TeamMember> savedTeamMembers = assertDoesNotThrow(() -> teamMemberService.saveAll(membersToSave));
        assertEquals(1, savedTeamMembers.size());
        verify(teamMemberRepository, times(1)).saveAll(membersToSave);
    }

    @Test
    void findByUserIdAndProjectIdFoundTest() {
        long userId = 1L;
        long projectId = 2L;
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(teamMember));

        TeamMember result = teamMemberService.findByUserIdAndProjectId(userId, projectId);

        assertEquals(teamMember, result);
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void findByUserIdAndProjectIdNotFoundTest() {
        long userId = 1L;
        long projectId = 2L;
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> teamMemberService.findByUserIdAndProjectId(userId, projectId));

        assertEquals("Entity %s with ID %s not found".formatted(TEAM_MEMBER, userId), exception.getMessage());
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void deleteTeamMemberTest() {
        TeamMember teamMember = new TeamMember();
        assertDoesNotThrow(() -> teamMemberService.delete(teamMember));
        verify(teamMemberRepository, times(1)).delete(teamMember);
    }

    @Test
    void deleteAllTeamMembersTest() {
        List<TeamMember> teamMembersToDelete = List.of(new TeamMember());
        assertDoesNotThrow(() -> teamMemberService.deleteAll(teamMembersToDelete));
        verify(teamMemberRepository, times(1)).deleteAll(teamMembersToDelete);
    }

    @Test
    public void teamMemberNotExistsTest() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.getTeamMemberEntity(id));
    }

    @Test
    public void teamMemberExistsTest() {
        long id = 1L;
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findById(id)).thenReturn(Optional.of(teamMember));

        assertDoesNotThrow(() -> teamMemberService.getTeamMemberEntity(id));
    }
}