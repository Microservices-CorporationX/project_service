package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

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
}