package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void saveInternshipTest() {
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