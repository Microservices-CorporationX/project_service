package faang.school.projectservice.service;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void getTeamMemberById() {
        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(teamMember);

        TeamMember result = teamMemberService.getTeamMemberById(teamMember.getId());

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
}
