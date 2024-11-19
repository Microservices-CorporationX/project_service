package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.stage.StageTaskStatusFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @InjectMocks
    private StageService stageService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @Spy
    private StageMapper stageMapper;

    @Mock
    private List<Filter> stageFilters;

    private Stage stage;
    private Stage anotherStage;
    private TeamMember teamMember;
    private Project project;
    private StageDto stageDto;
    private StageRoles stageRoles;
    private Task task;
    private StageFilterDto filters;
    private final long stageId = 1L;
    private final long userId = 1L;
    private final long projectId = 1L;
    private final long invalidProjectId = 0L;

    @BeforeEach
    public void setUp() {
        stageRoles = StageRoles.builder()
                .teamRole(TeamRole.DESIGNER)
                .build();

        task = Task
                .builder()
                .status(TaskStatus.DONE)
                .stage(stage)
                .build();

        stage = Stage
                .builder()
                .stageId(1L)
                .stageName("Stage 1")
                .stageRoles(List.of(stageRoles))
                .tasks(List.of(task))
                .project(
                        Project
                                .builder()
                                .id(1L)
                                .build()
                )
                .executors(new ArrayList<>())
                .build();

        anotherStage = Stage
                .builder()
                .stageId(2L)
                .stageName("Stage 2")
                .stageRoles(List.of(stageRoles))
                .tasks(List.of(task))
                .project(
                        Project
                                .builder()
                                .id(1L)
                                .build()
                )
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

        project = Project
                .builder()
                .id(1L)
                .name("Project 1")
                .stages(List.of(stage))
                .description("Description 1")
                .build();

        stageDto = StageDto.builder()
                .stageName("Stage 1")
                .projectId(1L)
                .stageRoles(new ArrayList<>())
                .build();

        filters = new StageFilterDto();
    }

    @Test
    public void testSetExecutor() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);

        stageService.setExecutor(stageId, userId);

        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).save(stage);
    }

    @Test
    public void testGetStageByIdSuccess() {
        when(stageRepository.getById(stageId)).thenReturn(stage);

        Stage stage = stageService.getStageById(stageId);
        assertEquals(1L, stage.getStageId());

        verify(stageRepository, times(1)).getById(stageId);
    }

    @Test
    void testExistsByIdShouldReturnTrueWhenStageExists() {
        Long stageId = 1L;
        when(stageRepository.existsById(stageId)).thenReturn(true);

        assertTrue(stageService.existsById(stageId));
    }

    @Test
    void testExistsByIdShouldReturnFalseWhenStageDoesNotExist() {
        Long stageId = 2L;
        when(stageRepository.existsById(stageId)).thenReturn(false);

        assertFalse(stageService.existsById(stageId));
    }

    @Test
    void testCreateStageThrowException() {
        when(projectService.getProjectById(projectId)).thenThrow(new EntityNotFoundException(
                String.format("Project not found by id: %s", projectId)));

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(1L),
                String.format("Project not found by id: %s", projectId));
    }

    @Test
    void testCreateStageSuccessfully() {
        when(stageMapper.toEntity(stageDto)).thenReturn(stage);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(stageDto);

        assertNotNull(result);
        assertEquals(stageDto, result);
    }

    @Test
    public void testGetStagesByProjectIdFilteredSuccess() {
        when(stageRepository.findAllByProjectId(projectId)).thenReturn(setUpStageList());
        Filter filter = mock(StageTaskStatusFilter.class);
        when(filter.isApplicable(filters)).thenReturn(true);
        when(filter.apply(any(Stream.class), eq(filters))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stageFilters.stream()).thenReturn(Stream.of(filter));
        when(stageMapper.toDto(stage)).thenReturn(stageDto);
        when(stageMapper.toDto(anotherStage)).thenReturn(new StageDto());

        List<StageDto> result = stageService.getStagesByProjectIdFiltered(projectId, filters);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetStagesByProjectIdFilteredEmptyProject() {
        when(stageRepository.findAllByProjectId(projectId)).thenReturn(List.of());

        List<StageDto> result = stageService.getStagesByProjectIdFiltered(projectId, filters);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllStagesByProjectIdShouldReturnExpectedStagesWhenProjectExists() {
        List<Stage> expectedStages = List.of(stage);

        when(stageRepository.findAllByProjectId(stage.getProject().getId())).thenReturn(expectedStages);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        List<StageDto> actualStageDtos = stageService.getStagesByProjectId(stage.getProject().getId());

        assertEquals(expectedStages.size(), actualStageDtos.size());
        assertEquals(stageDto, actualStageDtos.get(0));
        verify(stageMapper, times(expectedStages.size())).toDto(stage);
        verify(stageRepository, times(1)).findAllByProjectId(stage.getProject().getId());
    }

    @Test
    void testDeleteStageSuccess() {
        when(stageRepository.getById(stageId)).thenReturn(stage);

        stageService.deleteStage(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void testDeleteStageAndMoveTasksSuccess() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageRepository.getById(anotherStage.getStageId())).thenReturn(anotherStage);

        stageService.deleteStage(stageId, anotherStage.getStageId());

        assertEquals(stage.getTasks(), anotherStage.getTasks());
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).getById(anotherStage.getStageId());
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void testDeleteStageTasksIfAnotherStageDoesNotExistThrowEntityNotFoundException() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageRepository.getById(anotherStage.getStageId())).thenThrow(new EntityNotFoundException(
                String.format("Stage not found by id: %s", anotherStage.getStageId())));

        assertThrows(EntityNotFoundException.class, () ->
                        stageService.deleteStage(stageId, anotherStage.getStageId()),
                String.format("Stage not found by id: %s", anotherStage.getStageId()));
    }

    @Test
    void testDeleteStageIfStageDoesNotExistThrowEntityNotFoundException() {
        when(stageRepository.getById(stageId)).thenThrow(new EntityNotFoundException(
                String.format("Stage not found by id: %s", stageId)));

        assertThrows(EntityNotFoundException.class, () ->
                        stageService.deleteStage(stageId, anotherStage.getStageId()),
                String.format("Stage not found by id: %s", stageId));
    }

    @Test
    void testGetAllStagesByProjectIdShouldThrowEntityNotFoundExceptionWhenProjectDoesNotExist() {
        when(stageRepository.findAllByProjectId(invalidProjectId)).thenThrow(new EntityNotFoundException(
                String.format("Project not found by id: %s", invalidProjectId)));

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                stageService.getStagesByProjectId(invalidProjectId));

        assertEquals(String.format("Project not found by id: %s", invalidProjectId), exception.getMessage());
        verify(stageRepository, times(1)).findAllByProjectId(invalidProjectId);
    }

    @Test
    void testGetStageSuccess() {
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStageDtoById(stageId);

        assertEquals(stageDto, result);
    }

    @Test
    void testGetStageThrowEntityNotFoundException() {
        when(stageRepository.getById(stageId)).thenThrow(new EntityNotFoundException(
                String.format("Stage not found by id: %s", stageId)));

        assertThrows(EntityNotFoundException.class, () ->
                        stageService.getStageDtoById(stageId),
                String.format("Stage not found by id: %s", stage));
    }

    private List<Stage> setUpStageList() {
        return List.of(stage, anotherStage);
    }

}
