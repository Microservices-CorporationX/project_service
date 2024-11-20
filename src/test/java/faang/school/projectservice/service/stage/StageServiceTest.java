package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.ActionWithTask;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @InjectMocks
    private StageService stageService;

    @Mock
    private StageJpaRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Spy
    private StageMapperImpl stageMapper;

    @Mock
    private StageValidator stageValidator;

    @Mock
    private StageFilter stageFilter;

    @Mock
    List<StageFilter> filters;

    private Stage stage;
    private StageDto stageDto;
    private StageFilterDto stageFilterDto;
    private Project project;


    @BeforeEach
    public void setUp() {
        stageFilterDto = StageFilterDto.builder()
                .build();

        stageFilter = mock(StageFilter.class);

        filters = List.of(stageFilter);

        List<TeamMember> teamMembers = List.of(
                TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER, TeamRole.TESTER))
                        .build(),
                TeamMember.builder()
                        .id(2L)
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER, TeamRole.TESTER))
                        .build(),
                TeamMember.builder()
                        .id(3L)
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER, TeamRole.TESTER))
                        .build()
        );

        Team team = Team.builder()
                .id(1L)
                .teamMembers(teamMembers)
                .build();

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .teams(List.of(team))
                .build();

        List<StageRoles> stageRoles = List.of(
                StageRoles.builder().id(1L).count(10).teamRole(TeamRole.DESIGNER).build(),
                StageRoles.builder().id(2L).count(10).teamRole(TeamRole.DEVELOPER).build(),
                StageRoles.builder().id(3L).count(10).teamRole(TeamRole.TESTER).build()
        );

        List<Task> tasks = List.of(
                Task.builder().id(1L).description("Task 1").build(),
                Task.builder().id(2L).description("Task 2").build()
        );

        List<TeamMember> executors = List.of(
                TeamMember.builder().id(1L).roles(List.of(TeamRole.DESIGNER)).build(),
                TeamMember.builder().id(2L).roles(List.of(TeamRole.DEVELOPER)).build(),
                TeamMember.builder().id(3L).roles(List.of(TeamRole.TESTER)).build()
        );

        stage = Stage.builder()
                .stageId(1L)
                .stageName("Test Stage")
                .project(project)
                .stageRoles(stageRoles)
                .tasks(tasks)
                .executors(executors)
                .build();

        project.setStages(List.of(stage));

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("Test Stage")
                .projectId(1L)
                .stageRolesId(List.of(1L, 2L, 3L))
                .executorsId(List.of(1L, 2L, 3L))
                .build();
    }

    @Test
    @DisplayName("Verifying successful stage creation")
    public void checkCreateStageSuccessTest() {
        when(stageMapper.toEntity(stageDto))
                .thenReturn(stage);
        when(stageRepository.save(stage))
                .thenReturn(stage);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        StageDto createdStage = stageService.createStage(stageDto);

        assertNotNull(createdStage);

        verify(stageRepository, times(1)).save(stage);
    }

    @Test
    @DisplayName("Verifying successful stage acquisition with filtering")
    public void checkGetStageByFilterSuccessTest() {
        StageFilterDto filterDto = StageFilterDto.builder()
                .taskStatusPattern("todo")
                .teamRolePattern("owner")
                .build();

        Stage firstFilter = Stage.builder()
                .tasks(List.of(Task.builder().status(TaskStatus.TODO).build()))
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.OWNER).build()))
                .build();

        Stage secondFilter = Stage.builder()
                .tasks(List.of(Task.builder().status(TaskStatus.TODO).build()))
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.OWNER).build()))
                .build();

        List<Stage> stageList = List.of(firstFilter, secondFilter);

        when(stageRepository.findAll())
                .thenReturn(stageList);

        List<StageDto> result = stageService.getStageByFilter(filterDto);

        assertNotNull(result);

        verify(stageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Verifying successful user cascade deletion")
    public void checkDeleteUserSuccessCascadeTest() {
        ActionWithTask actionWithTask = ActionWithTask.CASCADE;
        Long transferStageId = 2L;

        when(stageRepository.getById(stage.getStageId())).thenReturn(stage);

        stageService.deleteStage(stage.getStageId(), actionWithTask, transferStageId);

        verify(stageRepository, times(1))
                .delete(stage);
    }

    @Test
    @DisplayName("Verifying successful user close deletion")
    public void checkDeleteUserSuccessCloseTest() {
        ActionWithTask actionWithTask = ActionWithTask.CLOSE;
        Long transferStageId = 2L;

        when(stageRepository.getById(stage.getStageId())).thenReturn(stage);

        stageService.deleteStage(stage.getStageId(), ActionWithTask.CLOSE, transferStageId);

        verify(stageRepository, times(1))
                .delete(stage);
    }

    @Test
    @DisplayName("Verifying successful user transfer deletion")
    public void checkDeleteUserSuccessTransferTest() {
        ActionWithTask actionWithTask = ActionWithTask.TRANSFER;
        Long transferStageId = 2L;

        Stage transferStage = Stage.builder()
                .stageId(2L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .description("Task 1")
                                .build(),
                        Task.builder()
                                .id(2L)
                                .description("Task 2")
                                .build()
                ))
                .build();

        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(stage);
        when(stageRepository.getById(transferStageId))
                .thenReturn(transferStage);

        stageService.deleteStage(stage.getStageId(), actionWithTask, transferStageId);

        verify(stageRepository, times(1))
                .save(transferStage);
        verify(stageRepository, times(1))
                .delete(stage);

    }

    @Test
    @DisplayName("Verifying successful stage update")
    public void checkUpdateStageSuccessTest() {
        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(stage);
        when(stageRepository.save(stage))
                .thenReturn(stage);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        StageDto updatedStage = stageService.updateStage(stageDto);

        assertNotNull(updatedStage);

        verify(stageRepository, times(1))
                .getById(stage.getStageId());
        verify(stageRepository, times(1))
                .save(stage);
    }

    @Test
    @DisplayName("Verification of successful receipt of all stages of the project")
    public void checkGetStagesByProjectSuccessTest() {
        when(projectRepository.getProjectById(project.getId()))
                .thenReturn(project);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        List<StageDto> stages = stageService.getAllProjectStages(project.getId());

        assertNotNull(stages);

        verify(projectRepository, times(1))
                .getProjectById(project.getId());
    }

    @Test
    @DisplayName("Checking the successful receipt of a stage by identifier")
    public void checkGetStageByIdSuccessTest() {
        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(stage);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        StageDto stageDto = stageService.getStageById(stage.getStageId());

        assertNotNull(stageDto);

        verify(stageRepository, times(1))
                .getById(stage.getStageId());
    }

}
