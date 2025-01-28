package faang.school.projectservice.service;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageService stageService;

    @Test
    public void shouldThrowNotFoundStage() {
        Long stageId = 5L;

        Mockito.when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stageService.findById(stageId),
                String.format("Этап c id: %d не найден", stageId));

        Mockito.verify(stageRepository, Mockito.times(1)).findById(stageId);
    }

    @Test
    public void shouldFoundTeamMember() {
        Long stageId = 5L;

        Stage stage = Stage.builder().stageId(stageId).build();

        Mockito.when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        Assertions.assertEquals(stage, stageService.findById(stageId));

        Mockito.verify(stageRepository, Mockito.times(1)).findById(stageId);
    }
}