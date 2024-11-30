package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.stage.Filter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.project.ProjectService;
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

import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.TESTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private StageInvitationService stageInvitationService;


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
    @DisplayName("Проверка getAllStagesByFilters - Успешно применен StageNameFilter")
    public void testGetAllStagesByFilters_StageNameFilter() {
        Filter<Stage, StageFilterDto> mockFilter = mock(Filter.class);
        List<Filter<Stage, StageFilterDto>> mockFilters = List.of(mockFilter);

        stageService = new StageService(stageRepository, stageMapper, mockFilters,
                taskService, projectService, stageInvitationService);

        StageDto responseDto = StageDto.builder()
                .stageName("Stage1")
                .build();

        StageFilterDto filterDto = StageFilterDto.builder()
                .stageName("Stage1")
                .build();

        List<Stage> stages = List.of(
                Stage.builder().stageName("Stage1").build(),
                Stage.builder().stageName("Stage2").build()
        );

        Stream<Stage> filteredStageStream = stages.subList(0, 1).stream();

        when(stageRepository.findAll()).thenReturn(stages);
        when(mockFilters.get(0).isApplicable(any(StageFilterDto.class))).thenReturn(true);
        when(mockFilters.get(0).apply(any(Stream.class), any(StageFilterDto.class))).thenReturn(filteredStageStream);
        when(stageMapper.toStageDto(any(Stage.class))).thenReturn(responseDto);

        List<StageDto> result = stageService.getAllStagesByFilters(filterDto);
        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toStageDto(any(Stage.class));
        verify(mockFilters.get(0), times(1)).apply(any(Stream.class), any(StageFilterDto.class));
        verify(mockFilters.get(0), times(1)).isApplicable(any(StageFilterDto.class));
        assertTrue(result.get(0).getStageName().equals("Stage1"));
        assertTrue(result.size() == 1);
    }

    @Test
    @DisplayName("Проверка getAllStagesByFilters - Успешно применен StageRolesFilter")
    public void testGetAllStagesByFilters_StageRolesFilter() {
        Filter<Stage, StageFilterDto> mockFilter = mock(Filter.class);
        List<Filter<Stage, StageFilterDto>> mockFilters = List.of(mockFilter);

        stageService = new StageService(stageRepository, stageMapper, mockFilters,
                taskService, projectService, stageInvitationService);

        StageDto responseDto = StageDto.builder()
                .stageName("Stage1")
                .stageRolesDto(List.of(StageRolesDto.builder().teamRole(MANAGER).build()))
                .build();

        StageFilterDto filterDto = StageFilterDto.builder()
                .teamRoles(List.of(MANAGER))
                .build();

        List<Stage> stages = List.of(
                Stage.builder()
                        .stageName("Stage1")
                        .stageRoles(List.of(StageRoles.builder().teamRole(MANAGER).build()))
                        .build(),

                Stage.builder()
                        .stageName("Stage2")
                        .stageRoles(List.of(StageRoles.builder().teamRole(TESTER).build()))
                        .build()
        );

        Stream<Stage> filteredStageStream = stages.subList(0, 1).stream();

        when(stageRepository.findAll()).thenReturn(stages);
        when(mockFilters.get(0).isApplicable(any(StageFilterDto.class))).thenReturn(true);
        when(mockFilters.get(0).apply(any(Stream.class), any(StageFilterDto.class))).thenReturn(filteredStageStream);
        when(stageMapper.toStageDto(any(Stage.class))).thenReturn(responseDto);

        List<StageDto> result = stageService.getAllStagesByFilters(filterDto);
        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toStageDto(any(Stage.class));
        verify(mockFilters.get(0), times(1)).apply(any(Stream.class), any(StageFilterDto.class));
        verify(mockFilters.get(0), times(1)).isApplicable(any(StageFilterDto.class));
        assertTrue(result.get(0).getStageName().equals("Stage1"));
        assertTrue(result.size() == 1);
    }

    @Test
    @DisplayName("Проверка getAllStagesByFilters - Успешно применен StageTaskFilter")
    public void testGetAllStagesByFilters_StageTaskFilter() {
        Filter<Stage, StageFilterDto> mockFilter = mock(Filter.class);
        List<Filter<Stage, StageFilterDto>> mockFilters = List.of(mockFilter);

        stageService = new StageService(stageRepository, stageMapper, mockFilters,
                taskService, projectService, stageInvitationService);

        StageDto responseDto = StageDto.builder()
                .stageName("Stage1")
                .tasks(List.of(Task.builder().status(TaskStatus.DONE).build()))
                .build();

        StageFilterDto filterDto = StageFilterDto.builder()
                .taskStatuses(List.of(TaskStatus.DONE))
                .build();

        List<Stage> stages = List.of(
                Stage.builder()
                        .stageName("Stage1")
                        .tasks(List.of(Task.builder().status(TaskStatus.DONE).build()))
                        .build(),

                Stage.builder()
                        .stageName("Stage2")
                        .tasks(List.of(Task.builder().status(TaskStatus.TODO).build()))
                        .build()
        );

        Stream<Stage> filteredStageStream = stages.subList(0, 1).stream();

        when(stageRepository.findAll()).thenReturn(stages);
        when(mockFilters.get(0).isApplicable(any(StageFilterDto.class))).thenReturn(true);
        when(mockFilters.get(0).apply(any(Stream.class), any(StageFilterDto.class))).thenReturn(filteredStageStream);
        when(stageMapper.toStageDto(any(Stage.class))).thenReturn(responseDto);

        List<StageDto> result = stageService.getAllStagesByFilters(filterDto);
        verify(stageRepository, times(1)).findAll();
        verify(stageMapper, times(1)).toStageDto(any(Stage.class));
        verify(mockFilters.get(0), times(1)).apply(any(Stream.class), any(StageFilterDto.class));
        verify(mockFilters.get(0), times(1)).isApplicable(any(StageFilterDto.class));
        assertTrue(result.get(0).getStageName().equals("Stage1"));
        assertTrue(result.size() == 1);
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
    @DisplayName("Проверка updateStage - Успешное обновление этапа с отправкой приглашений")
    public void updateStage_shouldUpdateStageRolesAndSendInvitations() {

        Long stageId = 1L;
        TeamRole roleManager = TeamRole.MANAGER;
        Stage stage = mock(Stage.class);
        Project project = mock(Project.class);
        Team team = mock(Team.class);
        TeamMember teamMember = mock(TeamMember.class);
        StageRoles stageRole = mock(StageRoles.class);

        List<StageRoles> stageRoles = List.of(stageRole);
        List<TeamMember> executors = new ArrayList<>();
        List<Team> teams = List.of(team);

        when(stage.getStageRoles()).thenReturn(stageRoles);
        when(stage.getExecutors()).thenReturn(executors);
        when(stage.getProject()).thenReturn(project);
        when(project.getTeams()).thenReturn(teams);

        // Настройка StageRoles
        when(stageRole.getTeamRole()).thenReturn(roleManager);
        when(stageRole.getCount()).thenReturn(2);

        // Настройка Team
        when(team.getTeamMembers()).thenReturn(List.of(teamMember));

        // Настройка TeamMember
        when(teamMember.getRoles()).thenReturn(List.of(roleManager));

        // Настройка репозитория
        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);

        doNothing().when(stageInvitationService).sendInvitation(any(StageInvitation.class));

        stageService.updateStage(stageId);

        // Проверки
        verify(stageRepository, times(1)).getById(stageId);
        verify(stageRepository, times(1)).save(stage);

        // Проверка вызова метода sendInvitation
        verify(stageInvitationService, times(1)).sendInvitation(any(StageInvitation.class));

        // Проверка метода getAbsenceTeamMembersByRole
        verify(stage, times(1)).getStageRoles();
        verify(stageRole, times(1)).getTeamRole();
        verify(stageRole, times(1)).getCount();

        // Утверждения
        assertTrue(stageRoles.size() > 0, "Stage should have roles defined");
        assertTrue(executors.isEmpty(), "Executors list should be initially empty");
        assertTrue(stage.getStageRoles().contains(stageRole), "Stage should contain the specified stage role");
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
