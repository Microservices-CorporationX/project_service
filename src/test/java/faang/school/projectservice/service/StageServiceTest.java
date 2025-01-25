package faang.school.projectservice.service;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
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
class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageService stageService;

    @Test
    void testGetStagesByIds() {
        Stage stage1 = new Stage();
        stage1.setStageId(1L);
        stage1.setStageName("Stage 1");

        Stage stage2 = new Stage();
        stage2.setStageId(2L);
        stage2.setStageName("Stage 2");

        when(stageRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(stage1, stage2));

        List<Stage> result = stageService.getStagesByIds(Arrays.asList(1L, 2L));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Stage 1", result.get(0).getStageName());
        assertEquals("Stage 2", result.get(1).getStageName());
        verify(stageRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
    }
}