package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Test
    public void testHasCuratorAccess_Curator() {
        // Arrange
        TeamMember curator = new TeamMember();
        curator.setRoles(List.of(TeamRole.OWNER));
        when(teamMemberRepository.findById(1L)).thenReturn(curator);

        // Act
        boolean result = teamMemberService.curatorHasNoAccess(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testHasCuratorAccess_NotCurator() {
        // Arrange
        TeamMember nonCurator = new TeamMember();
        nonCurator.setRoles(List.of(TeamRole.ANALYST));
        when(teamMemberRepository.findById(2L)).thenReturn(nonCurator);

        // Act
        boolean result = teamMemberService.curatorHasNoAccess(2L);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testGetTeamMemberValidId() {
        // arrange
        long id = 5L;
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findById(id)).thenReturn(teamMember);

        // act
        TeamMember returnedTeamMember = teamMemberService.getTeamMember(id);

        // assert
        assertEquals(teamMember, returnedTeamMember);
    }
}