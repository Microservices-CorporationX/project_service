package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.task.TaskService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private TaskService taskService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageInvitationService stageInvitationService;

    @Mock
    private List<StageFilter> stageFilterList;

    @InjectMocks
    private StageService stageService;

    private Stage stage;
    private Stage stage2;
    private StageDto stageDto;
    private StageDto stageDto2;
    private Project project;
    private List<Task> tasks;

    @BeforeEach
    void setUp() {
        stage = new Stage();
        stage.setStageId(1L);
        stage2 = new Stage();
        stage2.setStageId(2L);

        stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto2 = new StageDto();
        stageDto2.setStageId(2L);

        project = new Project();
        project.setId(100L);
        project.setStages(List.of(stage, stage2));
    }

    @Test
    @DisplayName("Проверка createStage - сохранили и вернули Dto")
    public void createStage_shouldSaveAndReturnDto() {

        when(stageMapper.toStage(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(stageDto);

        assertEquals(stageDto, result);

        verify(stageRepository, times(1)).save(stage);
        verify(stageMapper, times(1)).toStageDto(stage);
    }

//    @Test
//    public void getAllStagesByFilters_shouldApplyAllFiltersAndReturnDtos() {
//        StageFilter mockFilter = mock(StageFilter.class);
//        when(mockFilter.isApplicable(any(StageFilterDto.class))).thenReturn(true);
//        when(mockFilter.apply(any(Stream.class), any(StageFilterDto.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        stageFilterList = List.of(mockFilter);
//        when(stageRepository.findAll()).thenReturn(List.of(stage));
//        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);
//
//        List<StageDto> result = stageService.getAllStagesByFilters(new StageFilterDto());
//
//        assertEquals(1, result.size());
//        assertEquals(stageDto, result.get(0));
//
//        verify(mockFilter, times(1)).apply(any(Stream.class), any(StageFilterDto.class));
//    }

    @Test
    @DisplayName("Проверка deleteStageById - Смена статуса Task и удаление Stage")
    public void deleteStageById_shouldCancelTasksAndDeleteStage() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setStatus(TaskStatus.IN_PROGRESS);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setStatus(TaskStatus.TESTING);

        tasks = List.of(task1, task2);

        when(stageRepository.getById(1L)).thenReturn(stage);

        stageService.deleteStageById(1L);

        verify(stageRepository, times(1)).getById(1L);

        for (Task task : tasks) {
            assert task.getStatus() == TaskStatus.CANCELLED;
        }

        verify(taskService, times(1)).saveAll(tasks);
        verify(stageRepository, times(1)).delete(stage);
    }

//    @Test
//    public void updateStage_shouldSendInvitationsAndSaveStage() {
//
//        StageRoles mockRole = new StageRoles();
//        mockRole.setTeamRole("Developer");
//        mockRole.setCount(2L);
//
//        TeamMember teamMember = new TeamMember();
//        teamMember.setRoles(List.of("Developer"));
//
//        stage.setStageRoles(List.of(mockRole));
//        stage.setExecutors(List.of(teamMember));
//
//        when(stageRepository.getById(1L)).thenReturn(stage);
//        when(stageMapper.toStageDto(any(Stage.class))).thenReturn(stageDto);
//
//        StageDto result = stageService.updateStage(1L);
//
//        verify(stageInvitationService, times(1)).sendInvitation(any(StageInvitation.class));
//        verify(stageRepository, times(1)).save(stage);
//
//        assertEquals(stageDto, result);
//    }

    @Test
    @DisplayName("Проверка getAllStagesOfProject - Получение stages по project")
    public void getAllStagesOfProject_shouldReturnStagesOfProject() {

        when(projectService.getProject(100L)).thenReturn(project);
        when(stageMapper.toStageDtos(project.getStages())).thenReturn(List.of(stageDto, stageDto2));

        List<StageDto> result = stageService.getAllStagesOfProject(100L);

        assertEquals(List.of(stageDto, stageDto2), result);

        verify(projectService, times(1)).getProject(100L);
        verify(stageMapper, times(1)).toStageDtos(project.getStages());
    }

    @Test
    @DisplayName("Проверка getStageById - Получение Stage по id")
    public void getStageById_shouldReturnStageDto() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStageById(1L);

        assertEquals(stageDto, result);

        verify(stageRepository, times(1)).getById(1L);
        verify(stageMapper, times(1)).toStageDto(stage);
    }

}
