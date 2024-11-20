package faang.school.projectservice.service;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Test
    public void testGetStageValidId() {
        // arrange
        long id = 5L;
        Stage stage = new Stage();
        when(stageRepository.getById(id)).thenReturn(stage);

        // act
        Stage returnedStage = stageService.getStage(id);

        // assert
        assertEquals(stage, returnedStage);
    }
}
