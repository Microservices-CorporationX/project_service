package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.filter.stage.StageNameFilter;
import faang.school.projectservice.filter.stage.StageRolesFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    @DisplayName("Проверка сreateStage - Удачное создание этапа")
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
    @DisplayName("Проверка getAllStagesByFilters - Успешно применены реальные фильтры")
    public void testGetAllStagesByFilters_WithRealFilters() {
        StageFilterDto stageFilterDto = new StageFilterDto();
        stageFilterDto.setStageName("Stage A");

        StageRolesDto rolesDto1 = new StageRolesDto();
        rolesDto1.setStageRolesId(1L);

        stageFilterDto.setStageRolesDto(List.of(rolesDto1));

        StageRoles role1 = new StageRoles();
        role1.setId(1L);

        Stage matchingStage = new Stage();
        matchingStage.setStageName("Stage A");
        matchingStage.setStageRoles(List.of(role1));

        Stage nonMatchingStage1 = new Stage();
        nonMatchingStage1.setStageName("Stage B");
        nonMatchingStage1.setStageRoles(List.of(role1));

        Stage nonMatchingStage2 = new Stage();
        nonMatchingStage2.setStageName("Stage A");
        nonMatchingStage2.setStageRoles(List.of());

        List<Stage> stages = List.of(matchingStage, nonMatchingStage1, nonMatchingStage2);

        StageFilter nameFilter = new StageNameFilter();
        StageFilter rolesFilter = new StageRolesFilter();
        stageFilterList = List.of(nameFilter, rolesFilter);

        when(stageRepository.findAll()).thenReturn(stages);
        when(stageMapper.toStageDto(matchingStage)).thenReturn(stageDto);

        List<StageDto> result = stageService.getAllStagesByFilters(stageFilterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stageDto, result.get(0));

        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toStageDto(matchingStage);
        verify(stageMapper, never()).toStageDto(nonMatchingStage1);
        verify(stageMapper, never()).toStageDto(nonMatchingStage2);
    }



    @Test
    @DisplayName("Проверка getStageById - Успешно получили этап по id")
    public void testGetStageById_Success() {

        Long stageId = 1L;
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStageById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        assertEquals(stageDto, result);
    }

    @Test
    @DisplayName("Проверка deleteStageById - Успешное удаление этапа")
    public void deleteStageById_shouldCancelTasksAndDeleteStage() {
        Long stageId = 1L;
        Stage stage = new Stage();
        List<Task> tasks = new ArrayList<>();
        stage.setTasks(tasks);

        when(stageRepository.getById(stageId)).thenReturn(stage);

        stageService.deleteStageById(stageId);

        verify(taskService, times(1)).saveAll(tasks.stream()
                .peek(task -> task.setStatus(TaskStatus.CANCELLED))
                .toList());
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    @DisplayName("Проверка updateStage - Успешное обновление этапа")
    public void updateStage_shouldUpdateStageRoles() {
        Long stageId = 1L;
        Stage stage = new Stage();
        List<StageRoles> roles = new ArrayList<>();
        stage.setStageRoles(roles);

        when(stageRepository.getById(stageId)).thenReturn(stage);

        stageService.updateStage(stageId);

        verify(stageRepository, times(1)).save(stage);
    }

    @Test
    @DisplayName("Проверка getAllStagesOfProject - Успешное получение всех этапов проекта")
    public void getAllStagesOfProject_shouldReturnStageDto() {
        long projectId = 1L;
        ProjectDto projectDto = new ProjectDto();
        List<Stage> stages = new ArrayList<>();
        projectDto.setStages(stages);

        when(projectService.getProjectById(projectId)).thenReturn(projectDto);
        when(stageMapper.toStageDtos(stages)).thenReturn(new ArrayList<>());

        List<StageDto> result = stageService.getAllStagesOfProject(projectId);

        assertNotNull(result);
        verify(projectService, times(1)).getProjectById(projectId);
        verify(stageMapper, times(1)).toStageDtos(stages);
    }
}
