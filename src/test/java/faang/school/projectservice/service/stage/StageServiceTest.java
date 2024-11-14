package faang.school.projectservice.service.stage;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.stage.Stage;
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
public class StageServiceTest {

    @Mock
    StageJpaRepository repository;

    @InjectMocks
    StageService stageService;

    @Test
    public void stageExists() {
        long id = 1L;
        Stage stage = new Stage();
        stage.setStageId(id);
        when(repository.findById(id)).thenReturn(Optional.of(stage));

        Stage result = stageService.getStageEntity(id);

        assertEquals(stage.getStageId(), result.getStageId());
    }

    @Test
    public void throwsException() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class, () -> stageService.getStageEntity(id));
    }
}
