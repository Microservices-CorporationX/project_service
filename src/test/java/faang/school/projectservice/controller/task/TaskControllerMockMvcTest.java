package faang.school.projectservice.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(UserContext.class)
@AutoConfigureMockMvc
public class TaskControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private CreateUpdateTaskDto createTaskDto;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        createTaskDto = CreateUpdateTaskDto.builder()
                .name("task to create")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(1L)
                .minutesTracked(10)
                .parentTaskId(2L)
                .linkedTasksIds(new ArrayList<>(List.of(1L)))
                .stageId(1L)
                .build();

        taskDto = TaskDto.builder()
                .id(1L)
                .name("task to create")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .minutesTracked(10)

                .parentTaskId(2L)
                .linkedTasksIds(new ArrayList<>(List.of(1L)))
                .projectId(1L)
                .stageId(1L)
                .build();
    }

    @Test
    void createTaskClientErrorTest() throws Exception {
        mockMvc.perform(
                post("/tasks")
                        .header("x-team-member-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void createTaskTest() throws Exception {
        long creatorId = 1L;

        when(taskService.createTask(any(CreateUpdateTaskDto.class), anyLong())).thenReturn(taskDto);

        mockMvc.perform(
                        post("/tasks")
                                .header("x-team-member-id", creatorId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(createTaskDto))
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").
                        value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").
                        value("task to create"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").
                        value("description"));
    }
}