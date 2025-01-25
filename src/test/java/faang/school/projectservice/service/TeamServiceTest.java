package faang.school.projectservice.service;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void testGetTeamsByIds() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setAvatarKey("avatar1.png");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setAvatarKey("avatar2.png");

        when(teamRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(team1, team2));

        List<Team> result = teamService.getTeamsByIds(Arrays.asList(1L, 2L));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("avatar1.png", result.get(0).getAvatarKey());
        assertEquals("avatar2.png", result.get(1).getAvatarKey());
        verify(teamRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
    }
}