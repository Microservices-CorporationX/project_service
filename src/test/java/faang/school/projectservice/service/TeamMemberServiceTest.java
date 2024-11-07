package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

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
