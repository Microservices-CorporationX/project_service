package faang.school.projectservice;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;

import faang.school.projectservice.service.StageInvitationServiceImpl;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @Spy
    private StageMapperImpl stageMapper;

    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private StageInvitationServiceImpl stageInvitationService;

    @InjectMocks
    private StageService stageService;

    @Test
    public void testValidateWithNotExistingProject() {
        when(projectService.existsById(anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> stageService.validate(anyLong()));
    }

    @Test
    public void testValidateStatusCompleted() {
        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.getById(anyLong())).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.validate(anyLong()));
    }

    @Test
    public void testValidateStatusCancelled() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.getById(anyLong())).thenReturn(project);

        assertThrows(DataValidationException.class, () -> stageService.validate(anyLong()));
    }

    @Test
    public void testCreate() {
        StageDto stageDto = new StageDto();
        stageDto.setProjectId(1L);
        Stage stage = new Stage();

        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.getById(stageDto.getProjectId())).thenReturn(new Project());
        when(stageRepository.save(any())).thenReturn(stage);

        stageService.create(stageDto);

        verify(stageRepository).save(any());
    }

    @Test
    public void testGetByRolePositive() {
        StageDto stageDto = new StageDto();
        Long projectId = 1L;
        Long stageId = 1L;

        stageDto.setProjectId(projectId);
        stageDto.setStageId(stageId);

        TeamRole targetRole = TeamRole.DEVELOPER;
        StageRolesDto target = new StageRolesDto(2L, targetRole, 1);
        StageRolesDto roles = new StageRolesDto(1L, TeamRole.MANAGER, 1);

        stageDto.setStageRolesDto(List.of(new StageRolesDto(1L, TeamRole.MANAGER, 1),
                        new StageRolesDto(2L, TeamRole.DEVELOPER, 1))
                .stream().sorted(Comparator.comparing(r -> r.getTeamRole())).toList());


        List<StageDto> expected =
                List.of(StageDto.builder()
                        .stageId(stageId)
                        .stageRolesDto(List.of(target, roles)
                                .stream().sorted(Comparator.comparing(r -> r.getTeamRole())).toList()).build());


        Project project = Project.builder()
                .stages(List.of(stageMapper.toEntity(stageDto))).build();


        when(projectService.existsById(stageDto.getProjectId())).thenReturn(true);
        when(projectService.getById(projectId)).thenReturn(project);
        List<StageDto> result = stageService.getByRole(stageDto, targetRole);

        assertEquals(expected, result);
    }

    @Test
    public void testGetByRoleNegative() {
        StageDto stageDto = new StageDto();
        Long projectId = 1L;
        Long stageId = 1L;

        stageDto.setProjectId(projectId);
        stageDto.setStageId(stageId);
        TeamRole targetRole = TeamRole.OWNER;

        stageDto.setStageRolesDto(List.of(new StageRolesDto(1L, TeamRole.MANAGER, 1),
                        new StageRolesDto(2L, TeamRole.DEVELOPER, 1))
                .stream().sorted(Comparator.comparing(r -> r.getTeamRole())).toList());


        List<StageDto> expected = new ArrayList<>();

        Project project = Project.builder()
                .stages(List.of(stageMapper.toEntity(stageDto))).build();

        when(projectService.existsById(stageDto.getProjectId())).thenReturn(true);
        when(projectService.getById(projectId)).thenReturn(project);
        List<StageDto> result = stageService.getByRole(stageDto, targetRole);

        assertEquals(expected, result);
    }

    @Test
    public void testGetByStatusPositive() {
        TaskStatus targetStatus = TaskStatus.IN_PROGRESS;
        Long expectedStageId = 1L;
        Long stageId = 2L;
        Long projectId = 1L;

        Stage stage = new Stage();
        stage.setStageId(expectedStageId);
        Stage extraStage = new Stage();
        extraStage.setStageId(stageId);

        Task taskToDo = new Task();
        Task taskInProgress = new Task();

        taskToDo.setStatus(TaskStatus.IN_PROGRESS);
        taskInProgress.setStatus(TaskStatus.TODO);

        List<Task> tasks1 = new ArrayList<>();
        tasks1.add(taskToDo);
        List<Task> tasks2 = new ArrayList<>();
        tasks2.add(taskInProgress);

        stage.setTasks(tasks1);
        extraStage.setTasks(tasks2);

        List<Stage> stages = new ArrayList<>(List.of(stage, extraStage));

        Project project = new Project();
        project.setStages(stages);
        project.setId(projectId);

        StageDto stageDto = StageDto.builder().projectId(projectId).stageId(stageId).build();

        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.getById(anyLong())).thenReturn(project);

        List<StageDto> result = stageService.getByStatus(stageDto, targetStatus);
        List<StageDto> expected = new ArrayList<>(List.of(stageMapper.toDto(stage)));

        assertEquals(expected, result);
    }

    @Test
    public void testGetByStatusNegative() {
        TaskStatus targetStatus = TaskStatus.CANCELLED;
        Long expectedStageId = 1L;
        Long stageId = 2L;
        Long projectId = 1L;

        Stage stage = new Stage();
        stage.setStageId(expectedStageId);
        Stage extraStage = new Stage();
        extraStage.setStageId(stageId);

        Task taskToDo = new Task();
        Task taskInProgress = new Task();

        taskToDo.setStatus(TaskStatus.IN_PROGRESS);
        taskInProgress.setStatus(TaskStatus.TODO);

        List<Task> tasks1 = new ArrayList<>();
        tasks1.add(taskToDo);
        List<Task> tasks2 = new ArrayList<>();
        tasks2.add(taskInProgress);

        stage.setTasks(tasks1);
        extraStage.setTasks(tasks2);

        List<Stage> stages = new ArrayList<>(List.of(stage, extraStage));

        Project project = new Project();
        project.setStages(stages);
        project.setId(projectId);

        StageDto stageDto = StageDto.builder().projectId(projectId).stageId(stageId).build();

        when(projectService.existsById(anyLong())).thenReturn(true);
        when(projectService.getById(anyLong())).thenReturn(project);

        List<StageDto> result = stageService.getByStatus(stageDto, targetStatus);
        List<StageDto> expected = new ArrayList<>();

        assertEquals(expected, result);
    }

    @Test
    public void testDeleteCascade() {
        StageDto stageDto = new StageDto();
        stageDto.setProjectId(1L);
        stageDto.setStageId(1L);

        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(2L);

        Stage stage = new Stage();
        stage.setStageId(1L);
        stage.setTasks(List.of(task1, task2));

        Project project = new Project();
        project.setTasks(List.of(task1, task2));
        project.setStages(List.of(stage));

        when(projectService.existsById(1L)).thenReturn(true);
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(projectService.getById(1L)).thenReturn(project);

        StageDto result = stageService.deleteCascade(stageDto);

        assertEquals(stageDto, result);
        assertTrue(project.getTasks().isEmpty());
        assertTrue(project.getStages().isEmpty());

        verify(projectService).save(project);
    }

    @Test
    public void testPostponeTasks() {
        StageDto stageDto = new StageDto();
        stageDto.setProjectId(1L);
        stageDto.setStageId(1L);

        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(2L);

        Stage stage = new Stage();
        stage.setStageId(1L);
        stage.setTasks(List.of(task1, task2));
        Stage stage2 = new Stage();
        stage2.setStageId(2L);
        stage2.setTasks(new ArrayList<>());

        Project project = new Project();
        project.setTasks(List.of(task1, task2));
        project.setStages(List.of(stage, stage2));

        when(projectService.existsById(1L)).thenReturn(true);
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stage2);
        when(projectService.getById(1L)).thenReturn(project);

        stageService.postponeTasks(stageDto, 2L);

        assertTrue(project.getStages().size() == 1);
        assertTrue(stage2.getTasks().size() == 2);
    }

    @Test
    public void testSendMissingRolesInvite() {
        StageRolesDto role1 = new StageRolesDto(1L, TeamRole.MANAGER, 1);
        StageRolesDto role2 = new StageRolesDto(2L, TeamRole.DESIGNER, 1);

        List<StageRolesDto> roles = new ArrayList<>(List.of(role1, role2));
        List<StageRolesDto> rolesOrig = new ArrayList<>(List.of(role2));

        StageDto stageDto = new StageDto();
        Long projectId = 1L;
        Long stageId = 1L;

        stageDto.setProjectId(projectId);
        stageDto.setStageId(stageId);
        stageDto.setStageRolesDto(roles);

        TeamMember manager = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.MANAGER))).build();
        TeamMember designer = TeamMember.builder()
                .id(2L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER))).build();

        Team team = Team.builder().teamMembers(new ArrayList<>(List.of(manager, designer))).build();

        List<Team> teams = new ArrayList<>(List.of(team));

        Project project = Project.builder()
                .id(projectId)
                .ownerId(5L)
                .teams(teams)
                .stages(List.of(stageMapper.toEntity(stageDto))).build();

        when(projectService.getById(projectId)).thenReturn(project);

        stageService.sendMissingRolesInvite(roles, rolesOrig,stageDto);

        verify(stageInvitationService).sendStageInvitation(any());
    }

    @Test
    public void testNoMissingRolesInvite() {
        StageRolesDto role1 = new StageRolesDto(1L, TeamRole.MANAGER, 1);
        StageRolesDto role2 = new StageRolesDto(2L, TeamRole.DESIGNER, 1);

        List<StageRolesDto> roles = new ArrayList<>(List.of(role1, role2));
        List<StageRolesDto> rolesOrig = new ArrayList<>(List.of(role1, role2));

        StageDto stageDto = new StageDto();
        Long projectId = 1L;
        Long stageId = 1L;

        stageDto.setProjectId(projectId);
        stageDto.setStageId(stageId);
        stageDto.setStageRolesDto(roles);

        TeamMember manager = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.MANAGER))).build();
        TeamMember designer = TeamMember.builder()
                .id(2L)
                .roles(new ArrayList<>(List.of(TeamRole.DESIGNER))).build();

        Team team = Team.builder().teamMembers(new ArrayList<>(List.of(manager, designer))).build();

        List<Team> teams = new ArrayList<>(List.of(team));

        Project project = Project.builder()
                .id(projectId)
                .ownerId(5L)
                .teams(teams)
                .stages(List.of(stageMapper.toEntity(stageDto))).build();

        stageService.sendMissingRolesInvite(roles, rolesOrig,stageDto);

        verify(stageInvitationService, times(0)).sendStageInvitation(any());
    }

    @Test
    public void testGetNeededRolesMap() {
        StageRolesDto role1 = new StageRolesDto(1L, TeamRole.MANAGER, 2);
        StageRolesDto role2 = new StageRolesDto(2L, TeamRole.DESIGNER, 1);
        StageRolesDto role3 = new StageRolesDto(1L, TeamRole.MANAGER, 2);

        List<StageRolesDto> roles = new ArrayList<>(List.of(role1, role2));
        List<StageRolesDto> rolesOrig = new ArrayList<>(List.of(role3));

        Map<TeamRole, Integer> result = stageService.getNeededRolesMap(roles, rolesOrig);

        Map<TeamRole, Integer> expected = new HashMap<>();
        expected.put(TeamRole.DESIGNER, 1);

        assertEquals(expected, result);
    }

    @Test
    public void testGetNeededRolesMapEnoughRoles() {
        StageRolesDto role1 = new StageRolesDto(1L, TeamRole.MANAGER, 2);
        StageRolesDto role2 = new StageRolesDto(1L, TeamRole.MANAGER, 2);

        List<StageRolesDto> roles = new ArrayList<>(List.of(role1));
        List<StageRolesDto> rolesOrig = new ArrayList<>(List.of(role2));

        Map<TeamRole, Integer> result = stageService.getNeededRolesMap(roles, rolesOrig);
        Map<TeamRole, Integer> expected = new HashMap<>();

        assertEquals(expected, result);
    }
}
