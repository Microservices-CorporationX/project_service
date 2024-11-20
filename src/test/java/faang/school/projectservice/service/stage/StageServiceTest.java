package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.InvalidStageTransferException;
import faang.school.projectservice.exception.ProjectStatusValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.DeletionType;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private StageRolesMapper stageRolesMapper;

    @InjectMocks
    private StageService stageService;

    private static final String PROJECT_NOT_FOUND_MESSAGE = "Project not found by id: %s";
    private static final String PROJECT_STATUS_MESSAGE = "The project status is ";
    private static final String TARGET_STAGE_MESSAGE = "Target stage ID is required for task transfer";

    @Test
    void testCreateStageProjectNotFound() {
        StageDto stageDto = createStageDto(null, null, 1L, null);

        when(projectRepository.getProjectById(stageDto.getProjectId()))
                .thenThrow(new EntityNotFoundException(String.format("Project not found by id: %s", stageDto.getProjectId())));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                stageService.createStage(stageDto));

        verify(projectRepository, times(1)).getProjectById(stageDto.getProjectId());
        verifyNoMoreInteractions(projectRepository);

        assertEquals(String.format(PROJECT_NOT_FOUND_MESSAGE, stageDto.getProjectId()), entityNotFoundException.getMessage());
    }

    @Test
    void testCreateStageProjectStatusCancelled() {
        StageDto stageDto = createStageDto(null, null, 1L, null);
        Project project = createProject(1L, ProjectStatus.CANCELLED, null);

        when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);

        ProjectStatusValidationException projectStatusValidationException = assertThrows(ProjectStatusValidationException.class, () ->
                stageService.createStage(stageDto));

        verify(projectRepository, times(1)).getProjectById(stageDto.getProjectId());

        assertEquals(PROJECT_STATUS_MESSAGE + project.getStatus(), projectStatusValidationException.getMessage());
    }

    @Test
    void testCreateStageSuccessful() {
        StageDto stageDto = createStageDto(null, "Development", 1L, null);
        Project project = createProject(1L, ProjectStatus.IN_PROGRESS, null);
        Stage stageToSave = createStage(null, "Development", null, null, null);
        Stage savedStage = createStage(1L, "Development", project, null, null);
        StageDto resultStageDto = createStageDto(1L, "Development", 1L, null);

        when(projectRepository.getProjectById(stageDto.getProjectId())).thenReturn(project);
        when(stageMapper.toEntity(stageDto)).thenReturn(stageToSave);
        when(stageRepository.save(stageToSave)).thenReturn(savedStage);
        when(stageMapper.toDto(savedStage)).thenReturn(resultStageDto);

        StageDto result = stageService.createStage(stageDto);

        verify(projectRepository, times(1)).getProjectById(stageDto.getProjectId());
        verify(stageMapper, times(1)).toEntity(stageDto);
        verify(stageRepository, times(1)).save(stageToSave);
        verify(stageMapper, times(1)).toDto(savedStage);

        assertEquals(resultStageDto, result);
    }

    @Test
    void testGetFilteredProjectStages() {
        TeamRole teamRole = TeamRole.DEVELOPER;
        TaskStatus taskStatus = TaskStatus.DONE;
        StageRoles stageRole = createStageRole(teamRole, null);
        Task task = createTask(taskStatus);
        Stage stage = createStage(1L, null, null, List.of(stageRole), List.of(task));
        Project project = createProject(1L, null, List.of(stage));
        StageDto stageDto = createStageDto(1L, null, 1L, null);

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        List<StageDto> result = stageService.getFilteredProjectStages(1L, teamRole, taskStatus);

        verify(projectRepository, times(1)).getProjectById(1L);
        verify(stageMapper, times(1)).toDto(stage);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stageDto, result.get(0));
    }

    @Test
    void testDeleteDeletionTypeDelete() {
        Task task = createTask(TaskStatus.IN_PROGRESS);
        Stage stage = createStage(1L, null, null, null, List.of(task));

        when(stageRepository.getById(1L)).thenReturn(stage);

        stageService.delete(1L, DeletionType.DELETE, null);

        verify(stageRepository, times(1)).getById(1L);
        verify(taskRepository, times(1)).deleteAll(stage.getTasks());
        verify(stageRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDeletionTypeClose() {
        Task task = createTask(TaskStatus.IN_PROGRESS);
        Stage stage = createStage(1L, null, null, null, List.of(task));

        when(stageRepository.getById(1L)).thenReturn(stage);

        stageService.delete(1L, DeletionType.CLOSE, null);

        verify(stageRepository, times(1)).getById(1L);
        verify(taskRepository, times(1)).save(task);
        verify(stageRepository, times(1)).deleteById(1L);

        assertEquals(TaskStatus.CANCELLED, task.getStatus());
    }

    @Test
    void testDeleteDeletionTypeTransferTargetStageInvalid() {
        Stage stage = createStage(1L, null, null, null, null);

        when(stageRepository.getById(1L)).thenReturn(stage);

        InvalidStageTransferException invalidStageTransferException = assertThrows(InvalidStageTransferException.class, () ->
                stageService.delete(1L, DeletionType.TRANSFER, null));

        verify(stageRepository, times(1)).getById(1L);

        assertEquals(TARGET_STAGE_MESSAGE, invalidStageTransferException.getMessage());
    }

    @Test
    void testDeleteDeletionTypeTransfer() {
        Task task = createTask(TaskStatus.IN_PROGRESS);
        Stage stage = createStage(1L, null, null, null, List.of(task));
        Stage targetStage = createStage(2L, null, null, null, null);

        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(targetStage);

        stageService.delete(1L, DeletionType.TRANSFER, 2L);

        verify(stageRepository, times(1)).getById(1L);
        verify(stageRepository, times(1)).getById(2L);
        verify(taskRepository, times(1)).save(task);
        verify(stageRepository, times(1)).deleteById(1L);

        assertEquals(targetStage, task.getStage());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void testUpdateSuccessful() {
        StageRoles stageRole = createStageRole(TeamRole.TESTER, 1);
        StageRolesDto stageRolesDto = createStageRoleDto(TeamRole.TESTER, 1);
        StageRoles newStageRole = createStageRole(TeamRole.DEVELOPER, 2);
        StageRolesDto newStageRolesDto = createStageRoleDto(TeamRole.DEVELOPER, 2);
        Stage stageToUpdate = createStage(1L, "Initial Stage Name", null, List.of(stageRole), null);
        StageDto stageDto = createStageDto(1L, "Updated Stage Name", null, List.of(stageRolesDto, newStageRolesDto));
        Stage updatedStage = createStage(1L, "Updated Stage Name", null, List.of(stageRole, newStageRole), null);
        StageDto updatedStageDto = createStageDto(1L, "Updated Stage Name", 1L, List.of(stageRolesDto, newStageRolesDto));

        when(stageRepository.getById(1L)).thenReturn(stageToUpdate);
        when(stageRepository.save(stageToUpdate)).thenReturn(updatedStage);
        when(stageMapper.toDto(updatedStage)).thenReturn(updatedStageDto);

        StageDto result = stageService.update(1L, stageDto);

        verify(stageRepository, times(1)).getById(1L);
        verify(stageRepository, times(1)).save(stageToUpdate);
        verify(stageMapper, times(1)).toDto(updatedStage);
        verifyNoMoreInteractions(stageRepository, stageMapper);

        assertNotNull(result);
        assertEquals(updatedStageDto, result);
    }

    @Test
    void testGetAllStagesByProjectId() {
        Long projectId = 1L;
        Stage stage = createStage(1L, null, null, null, null);
        List<Stage> stages = List.of(stage);
        StageDto stageDto = createStageDto(1L, null, null, null);
        List<StageDto> stageDtos = List.of(stageDto);

        when(stageRepository.findAllStagesByProjectId(projectId)).thenReturn(stages);
        when(stageMapper.toDto(stages)).thenReturn(stageDtos);

        List<StageDto> result = stageService.getAllStagesByProjectId(projectId);

        verify(stageRepository, times(1)).findAllStagesByProjectId(projectId);
        verify(stageMapper, times(1)).toDto(stages);
        verifyNoMoreInteractions(stageRepository, stageMapper);

        assertNotNull(result);
        assertEquals(stageDtos, result);
    }

    @Test
    void testGetStageById() {
        Long stageId = 1L;
        Stage stage = createStage(1L, null, null, null, null);
        StageDto stageDto = createStageDto(1L, null, null, null);

        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStageById(stageId);

        verify(stageRepository, times(1)).getById(stageId);
        verify(stageMapper, times(1)).toDto(stage);
        verifyNoMoreInteractions(stageRepository, stageMapper);

        assertNotNull(result);
        assertEquals(stageDto, result);
    }

    private StageDto createStageDto(Long stageId, String stageName, Long projectId, List<StageRolesDto> stageRoles) {
        return new StageDto(stageId, stageName, projectId, stageRoles);
    }

    private Stage createStage(Long stageId, String stageName, Project project, List<StageRoles> stageRoles, List<Task> tasks) {
        Stage stage = new Stage();
        stage.setStageId(stageId);
        stage.setStageName(stageName);
        stage.setProject(project);
        stage.setStageRoles(stageRoles);
        stage.setTasks(tasks);
        return stage;
    }

    private Project createProject(Long projectId, ProjectStatus status, List<Stage> stages) {
        Project project = new Project();
        project.setId(projectId);
        project.setStatus(status);
        project.setStages(stages);
        return project;
    }

    private StageRoles createStageRole(TeamRole teamRole, Integer count) {
        StageRoles stageRole = new StageRoles();
        stageRole.setTeamRole(teamRole);
        stageRole.setCount(count);
        return stageRole;
    }

    private StageRolesDto createStageRoleDto(TeamRole teamRole, Integer count) {
        StageRolesDto stageRolesDto = new StageRolesDto();
        stageRolesDto.setTeamRole(teamRole);
        stageRolesDto.setCount(count);
        return stageRolesDto;
    }

    private Task createTask(TaskStatus taskStatus) {
        Task task = new Task();
        task.setStatus(taskStatus);
        return task;
    }
}
