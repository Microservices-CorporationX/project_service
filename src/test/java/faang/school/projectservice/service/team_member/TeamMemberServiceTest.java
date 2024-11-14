package faang.school.projectservice.service.team_member;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    TeamMemberJpaRepository repository;

    @InjectMocks
    TeamMemberService teamMemberService;

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
        when(repository.findById(id)).thenReturn(Optional.of(teamMember));

        TeamMember result = teamMemberService.getTeamMemberEntity(id);

        assertEquals(teamMember, result);
    }
}