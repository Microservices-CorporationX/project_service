package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.validator.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    private TaskService taskService;
    private TaskRepository taskRepository;
    private TaskMapper taskMapper;
    private TaskValidator taskValidator;
    private ProjectService projectService;
    private StageService stageService;
    private List<TaskFilter> filters;

    private long userId;
    private Task task;
    private Task firstTask;
    private Task secondTask;
    private long performerId;
    private long projectId;
    private String name;
    private TaskDto taskDto;
    private Project project;
    private TaskStatus status;
    private Long taskId;
    private List<Task> tasks;
    private TaskFilterDto filterDto;
    private UpdateTaskDto updateTaskDto;

    @BeforeEach
    public void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        taskMapper = Mockito.mock(TaskMapper.class);
        taskValidator = Mockito.mock(TaskValidator.class);
        projectService = Mockito.mock(ProjectService.class);
        stageService = Mockito.mock(StageService.class);
        TaskFilter taskFilter = Mockito.mock(TaskFilter.class);
        filters = List.of(taskFilter);

        taskService = new TaskService(
                taskRepository,
                taskMapper,
                taskValidator,
                filters,
                projectService,
                stageService
        );

        userId = 5L;
        performerId = 10L;
        projectId = 15L;
        name = "some name";
        status = TaskStatus.TODO;
        taskId = 20L;

        task = Task.builder()
                .id(taskId)
                .name(name)
                .performerUserId(performerId)
                .status(status)
                .build();
        taskDto = TaskDto.builder()
                .name(name)
                .performerUserId(performerId)
                .projectId(projectId)
                .build();
        firstTask = Task.builder()
                .status(TaskStatus.DONE)
                .build();
        secondTask = Task.builder()
                .status(TaskStatus.DONE)
                .build();
        tasks = List.of(firstTask, secondTask);
        project = Project.builder()
                .id(projectId)
                .tasks(tasks)
                .build();
        filterDto = TaskFilterDto
                .builder()
                .status(TaskStatus.DONE)
                .keyword("hi")
                .build();
        updateTaskDto = UpdateTaskDto
                .builder()
                .status(TaskStatus.DONE)
                .build();
    }

    @Test
    public void testCreateTask() {
        // arrange
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(projectService.findProjectById(taskDto.projectId())).thenReturn(project);

        // act
        taskService.createTask(userId, taskDto);

        // assert
        verify(taskMapper).toDto(task);
    }

    @Test
    public void testCreateTaskFailsValidation() {
        // arrange
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(projectService.findProjectById(taskDto.projectId())).thenReturn(project);
        doThrow(DataValidationException.class)
                .when(taskValidator)
                .validateCreateTask(task, userId);

        // act and assert
        assertThrows(DataValidationException.class,
                () -> taskService.createTask(userId, taskDto));
    }

    @Test
    public void testUpdateTask() {
        // arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // act
        taskService.updateTask(userId, taskId, updateTaskDto);

        // assert
        verify(taskMapper).toDto(task);
    }

    @Test
    public void testUpdateTaskFailsValidation() {
        // arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doThrow(AccessDeniedException.class)
                .when(taskValidator)
                .validateProjectMembership(task.getProject(), userId);

        // act and assert
        assertThrows(AccessDeniedException.class,
                () -> taskService.updateTask(userId, taskId, updateTaskDto));
    }

    @Test
    public void testGetProjectTasks() {
        // arrange
        when(projectService.findProjectById(projectId)).thenReturn(project);

        // act
        taskService.getProjectTasks(userId, projectId, filterDto);

        // assert
        verify(taskMapper).toDto(tasks);
    }

    @Test
    public void testGetProjectTasksFailsValidation() {
        // arrange
        when(projectService.findProjectById(projectId)).thenReturn(project);
        doThrow(AccessDeniedException.class)
                .when(taskValidator)
                .validateProjectMembership(project, userId);

        // act and assert
        assertThrows(AccessDeniedException.class,
                () -> taskService.getProjectTasks(userId, projectId, filterDto));
    }

    @Test
    public void testGetTask() {
        // arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // act
        taskService.getTask(userId, taskId);

        // assert
        verify(taskMapper).toDto(task);
    }

    @Test
    public void testGetTaskFailsValidation() {
        // arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doThrow(AccessDeniedException.class)
                .when(taskValidator)
                .validateProjectMembership(task.getProject(), userId);

        // act and assert
        assertThrows(AccessDeniedException.class,
                () -> taskService.getTask(userId, taskId));
    }
}
