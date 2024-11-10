package faang.school.projectservice.service.teammember;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Test
    public void findByIdNotFoundTest() {
        Long id = 1L;
        when(teamMemberRepository.findById(id)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> teamMemberService.findById(id));

        assertEquals("Team member doesn't exist by id: %d".formatted(id), exception.getMessage());
    }

    @Test
    public void findByIdFoundTest() {
        Long id = 1L;
        TeamMember teamMember = new TeamMember();
        teamMember.setId(id);
        teamMember.setUserId(10L);
        when(teamMemberRepository.findById(id)).thenReturn(Optional.of(teamMember));

        TeamMember findMember = teamMemberService.findById(id);

        assertEquals(teamMember.getId(), findMember.getId());
        assertEquals(teamMember.getUserId(), findMember.getUserId());
    }

    @Test
    void saveTeamMemberTest() {
        TeamMember teamMember = new TeamMember();
        teamMemberService.save(teamMember);

        verify(teamMemberRepository, times(1)).save(teamMember);
    }

    @Test
    void getByUserIdAndProjectIdNotNullTest() {
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findByUserIdAndProjectId(1L, 2L)).thenReturn(teamMember);

        TeamMember result = teamMemberService.getByUserIdAndProjectId(1L, 2L);

        assertEquals(teamMember, result);
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(1L, 2L);
    }

    @Test
    void getByUserIdAndProjectIdNullTest() {
        when(teamMemberRepository.findByUserIdAndProjectId(1L, 2L)).thenReturn(null);

        TeamMember result = teamMemberService.getByUserIdAndProjectId(1L, 2L);

        assertNull(result);
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(1L, 2L);
    }
}