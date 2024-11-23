package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private ProjectService projectService;

    @Mock
    private List<StageFilter> stageFilterList;

    @Mock
    private StageMapper stageMapper;

    @InjectMocks
    private StageService stageService;

    private Stage stage;
    private StageDto stageDto;

    @BeforeEach
    public void setUp() {
        stage = new Stage();
        stageDto = new StageDto();
    }

    @Test
    @DisplayName("Create Stage - Удачное создание этапа")
    public void testCreateStage_Success() {

        when(stageMapper.toStage(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(stageDto);

        verify(stageRepository, times(1)).save(stage);
        verify(stageMapper, times(1)).toStage(stageDto);
        verify(stageMapper, times(1)).toStageDto(stage);
        assertEquals(stageDto, result);
    }

    @Test
    @DisplayName("Get All Stages by Filters - вернули все этапы")
    public void testGetAllStagesByFilters_Success() {

        StageFilterDto stageFilterDto = new StageFilterDto();
        List<Stage> stages = List.of(stage);
        List<StageFilter> applicableFilters = List.of();

        when(stageRepository.findAll()).thenReturn(stages);
        when(stageFilterList.stream())
                .thenReturn(applicableFilters.stream());

        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        List<StageDto> result = stageService.getAllStagesByFilters(stageFilterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stageDto, result.get(0));
        verify(stageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get Stage by ID - Successfully Returned")
    public void testGetStageById_Success() {

        Long stageId = 1L;
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStageById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        assertEquals(stageDto, result);
    }
}
