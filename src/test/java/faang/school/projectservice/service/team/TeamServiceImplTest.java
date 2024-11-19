package faang.school.projectservice.service.team;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(teamMemberRepository.findByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(getTeamMember()));
    }

    @Test
    void deleteMemberByUserId() {
        teamService.deleteMemberByUserId(1L);
        Mockito.verify(teamMemberRepository).deleteByUserId(1L);
    }

    @Test
    void findMemberByUserIdAndProjectIdEmpty() {
        Mockito.lenient().when(teamMemberRepository.findByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        assertTrue(teamService.findMemberByUserIdAndProjectId(1L, 1L).isEmpty());
    }

    @Test
    void findMemberByUserIdAndProjectIdSuccess() {
        assertEquals(getTeamMember(), teamService.findMemberByUserIdAndProjectId(1L, 1L).get());
    }

    private TeamMember getTeamMember() {
        return new TeamMember();
    }
}