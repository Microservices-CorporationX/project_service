package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.validator.TaskValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskValidator taskValidator;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private List<Filter<Task, TaskFiltersDto>> taskFilters;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageService stageService;

    @Mock
    private Task task;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTaskTest() {
        CreateTaskDto createTaskDto = new CreateTaskDto();
        createTaskDto.setName("Test Task");
        createTaskDto.setDescription("Test Description");
        createTaskDto.setStatus(TaskStatus.TODO);
        createTaskDto.setPerformerUserId(1L);
        createTaskDto.setReporterUserId(1L);
        createTaskDto.setProjectId(1L);
        createTaskDto.setStageId(1L);

        doNothing().when(taskValidator).validateString(anyString());
        doNothing().when(taskValidator).validateUser(anyLong());
        doNothing().when(taskValidator).validateProject(anyLong());

        when(taskMapper.toEntity(createTaskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(ResponseTaskDto.builder().id(1L).name("Test Task").build());

        ResponseTaskDto result = taskService.create(createTaskDto);
        System.out.println(result);

        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTaskTest() {
        UpdateTaskDto updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setId(1L);
        updateTaskDto.setDescription("Updated Description");
        updateTaskDto.setStatus(TaskStatus.TODO);
        updateTaskDto.setPerformerUserId(1L);
        updateTaskDto.setParentTaskId(2L);

        Project project = new Project();
        project.setId(1L);

        Stage stage = new Stage();
        stage.setStageId(1L);

        Task task = new Task();
        task.setId(1L);
        task.setDescription("Initial Description");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPerformerUserId(1L);
        task.setParentTask(new Task());
        task.setProject(project);
        task.setStage(stage);

        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(ResponseTaskDto.builder().id(1L).description("Updated Description").status(TaskStatus.IN_PROGRESS).build());

        ResponseTaskDto result = taskService.update(updateTaskDto);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Updated Description", result.getDescription());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void filterTasksTest() {
        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setText("Test");

        List<Task> taskList = List.of(task);
        when(taskRepository.findTasksByProjectId(anyLong())).thenReturn(taskList);
        when(taskMapper.toDto(task)).thenReturn(ResponseTaskDto.builder().id(1L).build());

        var result = taskService.filterTasks(filters, 1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTaskByIdTest() {
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(ResponseTaskDto.builder().id(1L).build());

        ResponseTaskDto result = taskService.getTaskById(1L, 1L, 1L);

        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTasksTest() {
        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setText("Test");

        Task task = new Task();
        task.setId(1L);
        List<Task> taskList = List.of(task);

        when(taskRepository.findTasksByProjectId(1L, PageRequest.of(0, 10))).thenReturn(taskList);
        when(taskMapper.toDto(task)).thenReturn(ResponseTaskDto.builder().id(1L).build());

        var result = taskService.getTasks(1L, 1L, 10, 0);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(taskRepository, times(1)).findTasksByProjectId(1L, PageRequest.of(0, 10));
    }
}