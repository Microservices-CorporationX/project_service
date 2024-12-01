package faang.school.projectservice.service.task;

import static org.assertj.core.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.exception.task.AccessDeniedException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.task.filter.TaskFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskFilter statusFilter;
    @Mock
    private TaskFilter performerFilter;
    @Mock
    private TaskFilter keywordFilter;

    @Spy
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doNothing().when(taskService).validateUserAccessToProject(anyLong(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("Успешное создание задачи")
    public void testCreateTask_Success() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(1L);
        taskDTO.setUserId(1L);
        taskDTO.setName("Test Task");
        taskDTO.setReporterUserId(2L);
        taskDTO.setPerformerUserId(3L);

        Project project = new Project();
        project.setId(1L);

        TeamMember reporter = new TeamMember();
        reporter.setId(2L);

        TeamMember performer = new TeamMember();
        performer.setId(3L);

        Task task = new Task();
        task.setId(1L);

        doNothing().when(taskService).validateUserAccessToProject(anyLong(), anyLong(), anyLong());
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(teamMemberRepository.findById(2L)).thenReturn(reporter);
        when(teamMemberRepository.findById(3L)).thenReturn(performer);
        when(taskMapper.toEntity(taskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO createdTask = taskService.createTask(taskDTO, 1L);

        assertNotNull(createdTask);
        assertEquals(taskDTO.getName(), createdTask.getName());
    }

    @Test
    @DisplayName("Создание задачи — исполнитель не найден")
    public void testCreateTask_PerformerNotFound() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(1L);
        taskDTO.setUserId(1L);
        taskDTO.setName("Task with missing performer");
        taskDTO.setReporterUserId(2L);
        taskDTO.setPerformerUserId(99L);

        Project project = new Project();
        project.setId(1L);

        TeamMember reporter = new TeamMember();
        reporter.setId(2L);

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(teamMemberRepository.findById(2L)).thenReturn(reporter);
        when(teamMemberRepository.findById(99L))
            .thenThrow(new EntityNotFoundException("Team member doesn't exist by id: 99"));

        assertThrows(EntityNotFoundException.class, () -> {
            taskService.createTask(taskDTO, 1L);
        });
    }

    @Test
    @DisplayName("Ошибка при отсутствии проекта")
    public void testCreateTask_ProjectNotFound() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(999L);  // Несуществующий проект ID

        when(projectRepository.getProjectById(999L)).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> {
            taskService.createTask(taskDTO, 1L);
        });
    }

    @Test
    @DisplayName("Ошибка при отсутствии репортера")
    public void testCreateTask_ReporterNotFound() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(1L);
        taskDTO.setReporterUserId(999L);

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(teamMemberRepository.findById(999L)).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> {
            taskService.createTask(taskDTO, 1L);
        });
    }

    @Test
    @DisplayName("Ошибка доступа пользователя к проекту")
    public void testCreateTask_AccessDenied() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setProjectId(1L);
        taskDTO.setUserId(5L);

        doThrow(new AccessDeniedException("Access denied")).when(taskService)
            .validateUserAccessToProject(eq(1L), eq(1L), eq(5L));

        assertThrows(AccessDeniedException.class, () -> {
            taskService.createTask(taskDTO, 1L);
        });
    }

    @Test
    @DisplayName("Успешное обновление задачи")
    public void testUpdateTask_Success() {
        Long taskId = 1L;
        Long projectId = 1L;

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Updated Task");
        taskDTO.setUserId(1L);
        taskDTO.setReporterUserId(2L);
        taskDTO.setPerformerUserId(3L);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        Project project = new Project();
        project.setId(projectId);
        existingTask.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskService).validateUserAccessToProject(projectId, projectId, 1L);
        when(taskRepository.save(existingTask)).thenReturn(existingTask);
        when(taskMapper.toDto(existingTask)).thenReturn(taskDTO);

        TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO, projectId);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getName());
    }

    @Test
    @DisplayName("Обновление задачи — задача не найдена")
    public void testUpdateTask_TaskNotFound() {
        Long taskId = 1L;
        TaskDTO taskDTO = new TaskDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTask(taskId, taskDTO, 1L);
        });
    }

    @Test
    @DisplayName("Обновление задачи — нет доступа к проекту")
    public void testUpdateTask_AccessDenied() {
        Long taskId = 1L;
        Long projectId = 1L;

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setUserId(1L);

        Task existingTask = new Task();
        Project project = new Project();
        project.setId(projectId);
        existingTask.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doThrow(new AccessDeniedException("У пользователя нет доступа к этому проекту"))
            .when(taskService).validateUserAccessToProject(projectId, projectId, 1L);

        assertThrows(AccessDeniedException.class, () -> {
            taskService.updateTask(taskId, taskDTO, projectId);
        });
    }

    @Test
    @DisplayName("Обновление задачи — невалидные данные")
    public void testUpdateTask_InvalidData() {
        Long taskId = 1L;
        TaskDTO taskDTO = new TaskDTO();

        Task existingTask = new Task();
        existingTask.setId(taskId);
        Project project = new Project();
        existingTask.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskService).validateUserAccessToProject(anyLong(), anyLong(), anyLong());

        doThrow(new RuntimeException("Invalid data"))
            .when(taskMapper).updateTaskFromDto(any(TaskDTO.class), any(Task.class));

        assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(taskId, taskDTO, 1L);
        });
    }
}
