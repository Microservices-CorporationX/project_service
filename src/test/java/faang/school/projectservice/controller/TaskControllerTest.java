package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    private Long projectId = 1L;
    private Long taskId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createTaskTest() throws Exception {
        CreateTaskDto createTaskDto = new CreateTaskDto();
        createTaskDto.setName("Test Task");
        createTaskDto.setDescription("Test Description");
        createTaskDto.setStatus(TaskStatus.TODO);
        createTaskDto.setPerformerUserId(1L);
        createTaskDto.setReporterUserId(1L);
        createTaskDto.setProjectId(1L);
        createTaskDto.setStageId(1L);

        ResponseTaskDto responseTaskDto = ResponseTaskDto.builder().id(1L).name("Test Task").build();

        when(taskService.create(any(CreateTaskDto.class))).thenReturn(responseTaskDto);

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .header("x-user-id", 1L)
                        .content("{\"name\":\"Test Task\",\"description\":\"Test Description\",\"status\":\"TODO\",\"performerUserId\":1,\"reporterUserId\":1,\"projectId\":1,\"stageId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Task"));

        verify(taskService, times(1)).create(any(CreateTaskDto.class));
    }

    @Test
    void updateTaskTest() throws Exception {
        UpdateTaskDto updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setId(1L);
        updateTaskDto.setDescription("Updated description");
        updateTaskDto.setStatus(TaskStatus.IN_PROGRESS);
        updateTaskDto.setPerformerUserId(2L);

        ResponseTaskDto responseTaskDto = ResponseTaskDto.builder().id(1L).build();

        when(taskService.update(any(UpdateTaskDto.class))).thenReturn(responseTaskDto);

        mockMvc.perform(put("/tasks")
                        .contentType("application/json")
                        .content("{\"id\":1,\"description\":\"Updated description\",\"status\":\"IN_PROGRESS\",\"performerUserId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).update(any(UpdateTaskDto.class));
    }

    @Test
    void filterTasksSuccess() throws Exception {
        TaskFiltersDto filters = new TaskFiltersDto(TaskStatus.TODO, "Test", 1L);
        List<ResponseTaskDto> taskDtos = List.of(ResponseTaskDto.builder().id(1L).build(), ResponseTaskDto.builder().id(2L).build());

        when(userContext.getUserId()).thenReturn(1L);
        when(taskService.filterTasks(filters, userId, projectId)).thenReturn(taskDtos);

        mockMvc.perform(get("/tasks/filters/1")
                        .param("projectId", "1")
                        .param("status", "TODO")
                        .param("performerUserId", "1")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(taskService, times(1)).filterTasks(any(TaskFiltersDto.class), eq(1L), eq(1L));
    }

    @Test
    void getTasksSuccess() throws Exception {
        List<ResponseTaskDto> taskDto = List.of(ResponseTaskDto.builder().id(1L).build(), ResponseTaskDto.builder().id(2L).build());


        when(userContext.getUserId()).thenReturn(1L);
        when(taskService.getTasks(userId, projectId, 10, 0)).thenReturn(taskDto);

        mockMvc.perform(get("/tasks/1")
                        .param("projectId", "1")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(taskService, times(1)).getTasks(1L, 1L, 10, 0);
    }

    @Test
    void getTasksNoContent() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);
        when(taskService.getTasks(userId, projectId, 10, 0)).thenReturn(List.of());

        mockMvc.perform(get("/tasks/1")
                        .param("projectId", "1")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).getTasks(1L, 1L, 10, 0);
    }

    @Test
    void getTasksByIdSuccess() throws Exception {
        ResponseTaskDto taskDto = ResponseTaskDto.builder().id(1L).build();

        when(userContext.getUserId()).thenReturn(1L);
        when(taskService.getTaskById(userId, projectId, 1L)).thenReturn(taskDto);

        mockMvc.perform(get("/tasks/1/1")
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(taskService, times(1)).getTaskById(1L, 1L, 1L);
    }

    @Test
    void getTasksByIdNotFound() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);
        when(taskService.getTaskById(userId, projectId, 1L)).thenReturn(null);

        mockMvc.perform(get("/tasks/1/1")
                        .param("projectId", "1"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).getTaskById(1L, 1L, 1L);
    }
}