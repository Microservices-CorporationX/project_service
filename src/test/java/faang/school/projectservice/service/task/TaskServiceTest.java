package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.exception.task.AccessDeniedException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.task.filter.TaskFilter;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TaskMapper taskMapper;

    private TaskDTO taskDTO;
    private Task task;
    private Project project;
    private TeamMember reporter;
    private TeamMember performer;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);

        reporter = new TeamMember();
        reporter.setId(1L);

        performer = new TeamMember();
        performer.setId(2L);

        taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setProjectId(1L);
        taskDTO.setReporterUserId(1L);
        taskDTO.setPerformerUserId(2L);

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setProject(project);
        task.setReporterUserId(reporter.getId());
        task.setPerformerUserId(performer.getId());
    }

    @Test
    @DisplayName("Создание задачи: возвращает TaskDTO при успешном создании")
    void createTask_ShouldReturnTaskDTO() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(teamMemberRepository.findById(1L)).thenReturn(reporter);
        when(teamMemberRepository.findById(2L)).thenReturn(performer);
        when(teamMemberRepository.isUserInAnyTeamOfProject(1L, 1L)).thenReturn(true);
        when(taskMapper.toEntity(taskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO createdTask = taskService.createTask(taskDTO, 1L);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getName());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Создание задачи: выбрасывает AccessDeniedException при отсутствии доступа")
    void createTask_ShouldThrowAccessDeniedException_WhenUserHasNoAccess() {
        when(teamMemberRepository.isUserInAnyTeamOfProject(1L, 3L)).thenReturn(false);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            taskService.createTask(taskDTO, 3L)
        );
        assertEquals("У вас нет доступа к проекту с ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление задачи: возвращает обновлённый TaskDTO при успешном обновлении")
    void updateTask_ShouldReturnUpdatedTaskDTO() {
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setName("Updated Task");

        when(teamMemberRepository.isUserInAnyTeamOfProject(1L, 1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(updatedTaskDTO);
        when(taskRepository.save(task)).thenReturn(task);

        TaskDTO updatedTask = taskService.updateTask(1L, updatedTaskDTO, 1L);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getName());
    }

    @Test
    @DisplayName("Обновление задачи: выбрасывает IllegalArgumentException при отсутствии задачи")
    void updateTask_ShouldThrowIllegalArgumentException_WhenTaskNotFound() {
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setName("Updated Task");

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            taskService.updateTask(1L, updatedTaskDTO, 1L)
        );
        assertEquals("Задача с таким ID не найдена", exception.getMessage());
    }

    @Test
    @DisplayName("Получение задач с фильтром: возвращает отфильтрованные задачи")
    void getFilteredTasks_ShouldReturnFilteredTasks() {
        TaskFilterDTO filterDTO = new TaskFilterDTO();
        filterDTO.setProjectId(1L);

        when(teamMemberRepository.isUserInAnyTeamOfProject(1L, 1L)).thenReturn(true);

        TaskFilter taskFilter = mock(TaskFilter.class);
        when(taskFilter.isApplicable(filterDTO)).thenReturn(true);
        when(taskFilter.apply(any(), eq(filterDTO))).thenReturn(Arrays.asList(task).stream());

        List<TaskFilter> filters = Arrays.asList(taskFilter);

        TaskService taskService = new TaskService(taskRepository, projectRepository, teamMemberRepository, taskMapper, filters);

        when(taskRepository.findAllByProjectId(1L)).thenReturn(Arrays.asList(task));

        List<TaskDTO> filteredTasks = taskService.getFilteredTasks(filterDTO, 1L);

        assertNotNull(filteredTasks);
        assertEquals(1, filteredTasks.size());
    }

    @Test
    @DisplayName("Получение задач с фильтром: выбрасывает ValidationException при отсутствии фильтра")
    void getFilteredTasks_ShouldThrowValidationException_WhenFilterIsNull() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
            taskService.getFilteredTasks(null, 1L)
        );
        assertEquals("Фильтр не может быть null", exception.getMessage());
    }

    @Test
    @DisplayName("Получение задачи по ID: возвращает TaskDTO при успешном поиске")
    void getTaskById_ShouldReturnTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");

        when(teamMemberRepository.isUserInAnyTeamOfProject(1L, 1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        TaskDTO foundTask = taskService.getTaskById(1L, 1L);

        assertNotNull(foundTask);
        assertEquals("Test Task", foundTask.getName());
    }

    @Test
    @DisplayName("Получение задачи по ID: выбрасывает IllegalArgumentException при отсутствии задачи")
    void getTaskById_ShouldThrowIllegalArgumentException_WhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            taskService.getTaskById(1L, 1L)
        );
        assertEquals("Задача с таким ID не найдена", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка доступа пользователя к проекту: выбрасывает AccessDeniedException при отсутствии доступа")
    void validateUserAccessToProject_ShouldThrowAccessDeniedException_WhenUserHasNoAccess() {
        when(teamMemberRepository.isUserInAnyTeamOfProject(1L, 3L)).thenReturn(false);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            taskService.validateUserAccessToProject(1L, 3L)
        );
        assertEquals("У вас нет доступа к проекту с ID: 1", exception.getMessage());
    }
}
