package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.exception.task.AccessDeniedException;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    private TaskDTO taskDTO;
    private TaskFilterDTO taskFilterDTO;

    @BeforeEach
    void setUp() {
        taskDTO = new TaskDTO();
        taskDTO.setName("Test Task");
        taskDTO.setProjectId(1L);
        taskDTO.setReporterUserId(1L);
        taskDTO.setPerformerUserId(2L);
        taskFilterDTO = new TaskFilterDTO();
        taskFilterDTO.setProjectId(1L);
    }

    @Test
    @DisplayName("Создание задачи: возвращает TaskDTO при успешном создании задачи")
    void createTask_ShouldReturnCreatedTaskDTO() {
        when(taskService.createTask(taskDTO, 1L)).thenReturn(taskDTO);
        TaskDTO response = taskController.createTask(1L, taskDTO);
        assertNotNull(response);
        assertEquals("Test Task", response.getName());
    }

    @Test
    @DisplayName("Создание задачи: выбрасывает AccessDeniedException, если у пользователя нет доступа")
    void createTask_ShouldThrowAccessDeniedException_WhenUserHasNoAccess() {
        when(taskService.createTask(taskDTO, 3L)).thenThrow(new AccessDeniedException("У вас нет доступа к проекту с ID: 1"));
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
            taskController.createTask(3L, taskDTO)
        );
        assertEquals("У вас нет доступа к проекту с ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление задачи: возвращает обновлённую задачу")
    void updateTask_ShouldReturnUpdatedTaskDTO() {
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setName("Updated Task");
        when(taskService.updateTask(1L, updatedTaskDTO, 1L)).thenReturn(updatedTaskDTO);
        TaskDTO response = taskController.updateTask(1L, 1L, updatedTaskDTO);
        assertNotNull(response);
        assertEquals("Updated Task", response.getName());
    }

    @Test
    @DisplayName("Обновление задачи: выбрасывает IllegalArgumentException, если задача не найдена")
    void updateTask_ShouldThrowIllegalArgumentException_WhenTaskNotFound() {
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setName("Updated Task");
        when(taskService.updateTask(1L, updatedTaskDTO, 1L)).thenThrow(new IllegalArgumentException("Задача с таким ID не найдена"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            taskController.updateTask(1L, 1L, updatedTaskDTO)
        );
        assertEquals("Задача с таким ID не найдена", exception.getMessage());
    }

    @Test
    @DisplayName("Получение задачи по ID: возвращает TaskDTO при успешном получении задачи")
    void getTaskById_ShouldReturnTaskDTO() {
        when(taskService.getTaskById(1L, 1L)).thenReturn(taskDTO);
        TaskDTO response = taskController.getTaskById(1L, 1L);
        assertNotNull(response);
        assertEquals("Test Task", response.getName());
    }

    @Test
    @DisplayName("Получение задачи по ID: выбрасывает IllegalArgumentException, если задача не найдена")
    void getTaskById_ShouldThrowIllegalArgumentException_WhenTaskNotFound() {
        when(taskService.getTaskById(1L, 1L)).thenThrow(new IllegalArgumentException("Задача с таким ID не найдена"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            taskController.getTaskById(1L, 1L)
        );
        assertEquals("Задача с таким ID не найдена", exception.getMessage());
    }

    @Test
    @DisplayName("Фильтрация задач: возвращает список задач при фильтрации")
    void getFilteredTasks_ShouldReturnFilteredTasks() {
        when(taskService.getFilteredTasks(taskFilterDTO, 1L)).thenReturn(Collections.singletonList(taskDTO));
        List<TaskDTO> response = taskController.getFilteredTasks(1L, taskFilterDTO);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Task", response.get(0).getName());
    }

    @Test
    @DisplayName("Фильтрация задач: выбрасывает ValidationException, если фильтр null")
    void getFilteredTasks_ShouldThrowValidationException_WhenFilterIsNull() {
        when(taskService.getFilteredTasks(null, 1L)).thenThrow(new ValidationException("Фильтр не может быть null"));
        ValidationException exception = assertThrows(ValidationException.class, () ->
            taskController.getFilteredTasks(1L, null)
        );
        assertEquals("Фильтр не может быть null", exception.getMessage());
    }
}
