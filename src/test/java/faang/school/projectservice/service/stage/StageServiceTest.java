package faang.school.projectservice.service.stage;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberService teamMemberService;

    private Stage stage;
    private TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        stage = Stage
                .builder()
                .stageId(1L)
                .stageName("Stage 1")
                .executors(new ArrayList<>())
                .build();

        teamMember = TeamMember
                .builder()
                .userId(1L)
                .userId(1L)
                .team(
                        Team
                                .builder()
                                .teamMembers(List.of())
                                .build()
                )
                .stages(List.of(stage))
                .build();
    }

    @Test
    public void setExecutor() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(1L)).thenReturn(teamMember);

        stageService.setExecutor(1L, 1L);

        verify(stageRepository, times(1)).getById(1L);
        verify(stageRepository, times(1)).save(stage);
    }

    @Test
    public void getById() {
        when(stageRepository.getById(1L)).thenReturn(stage);

        Stage stage = stageService.getById(1L);
        assertEquals(1L, stage.getStageId());

        verify(stageRepository, times(1)).getById(1L);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenStageExists() {
        Long stageId = 1L;
        when(stageRepository.existsById(stageId)).thenReturn(true);

        assertTrue(stageService.existsById(stageId));
    }

    @Test
    void existsById_ShouldReturnFalse_WhenStageDoesNotExist() {
        Long stageId = 2L;
        when(stageRepository.existsById(stageId)).thenReturn(false);

        assertFalse(stageService.existsById(stageId));
    }
}