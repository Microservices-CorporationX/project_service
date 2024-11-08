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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> teamService.findMemberByUserIdAndProjectId(1L, 1L));
        assertEquals("Member not found by project id %s and user id %s".formatted(1L, 1L), exception.getMessage());
    }

    @Test
    void findMemberByUserIdAndProjectIdSuccess() {
        assertEquals(getTeamMember(), teamService.findMemberByUserIdAndProjectId(1L, 1L));
    }

    private TeamMember getTeamMember() {
        return new TeamMember();
    }
}