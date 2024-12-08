package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageControllerTest {

    @Mock
    private StageService stageService;

    @InjectMocks
    private StageController stageController;

    private StageDto stageDto;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    public void setUp() {
        stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto.setStageName("Test Stage");
        stageFilterDto = new StageFilterDto();
    }

    @Test
    @DisplayName("Проверка createStage - создали и вернули stage")
    public void testCreateStage_returnCreatedStage() {

        when(stageService.createStage(stageDto)).thenReturn(stageDto);

        StageDto result = stageController.createStage(stageDto);

        assertEquals(stageDto, result);

        verify(stageService, times(1)).createStage(stageDto);
    }

    @Test
    @DisplayName("Проверка getAllStagesByFilters - вернули список stage")
    public void testGetAllStagesByFilters_returnEmptyStage() {

        List<StageDto> stages = List.of(stageDto);
        when(stageService.getAllStagesByFilters(stageFilterDto)).thenReturn(stages);

        List<StageDto> result = stageController.getAllStagesByFilters(stageFilterDto);

        assertEquals(stages, result);

        verify(stageService, times(1)).getAllStagesByFilters(stageFilterDto);
    }

    @Test
    @DisplayName("Проверка deleteStageById - удалили stage")
    public void testDeleteStageById_returnDeletedStage() {

        Long stageId = 1L;

        stageController.deleteStageById(stageId);

        verify(stageService, times(1)).deleteStageById(stageId);
    }

    @Test
    @DisplayName("Проверка updateStage - вернули обновленный stage")
    public void testUpdateStage_returnUpdatedStage() {

        Long stageId = 1L;

        when(stageService.updateStage(stageId)).thenReturn(stageDto);

        StageDto result = stageController.updateStage(stageId);

        assertEquals(stageDto, result);

        verify(stageService, times(1)).updateStage(stageId);
    }

    @Test
    @DisplayName("Проверка getAllStages - вернули список stage")
    public void testGetAllStages_returnStages() {
        Long projectId = 1L;
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getAllStagesOfProject(projectId)).thenReturn(stages);

        List<StageDto> result = stageController.getAllStages(projectId);

        assertEquals(stages, result);

        verify(stageService, times(1)).getAllStagesOfProject(projectId);
    }

    @Test
    @DisplayName("Проверка getStageById - stage")
    public void testGetStageById_returnStage() {
        Long stageId = 1L;

        when(stageService.getStageById(stageId)).thenReturn(stageDto);

        StageDto result = stageController.getStageById(stageId);

        assertEquals(stageDto, result);

        verify(stageService, times(1)).getStageById(stageId);
    }

}
