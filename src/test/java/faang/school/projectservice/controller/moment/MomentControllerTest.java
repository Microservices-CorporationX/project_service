package faang.school.projectservice.controller.moment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {MomentController.class})
public class MomentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MomentService momentService;
    @Autowired
    private ObjectMapper objectMapper;
    private MomentDto momentDto;
    private MomentFilterDto momentFilterDto;

    @BeforeEach
    void setUp() {
        momentDto = new MomentDto();
        momentDto.setName("Test Moment");
        momentDto.setDescription("Test Description");
        momentDto.setDate(LocalDateTime.now());
        momentDto.setImageId("imageId");
        momentDto.setProjectIds(Arrays.asList(1L, 2L));
        momentDto.setTeamMemberIds(Arrays.asList(1L, 2L));
    }

    @Test
    void testCreateMoment() throws Exception {
        when(momentService.createMoment(any(MomentDto.class))).thenReturn(momentDto);

        mockMvc.perform(post("/moment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Moment"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.imageId").value("imageId"))
                .andExpect(jsonPath("$.projectIds", hasSize(2)))
                .andExpect(jsonPath("$.projectIds", contains(1, 2)))
                .andExpect(jsonPath("$.teamMemberIds", hasSize(2)))
                .andExpect(jsonPath("$.teamMemberIds", contains(1, 2)));
    }

    @Test
    void testUpdateMoment() throws Exception {
        Long momentId = 1L;
        when(momentService.updateMoment(eq(momentId), any(MomentDto.class))).thenReturn(momentDto);

        mockMvc.perform(put("/moment/{id}", momentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Moment"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.imageId").value("imageId"))
                .andExpect(jsonPath("$.projectIds", hasSize(2)))
                .andExpect(jsonPath("$.projectIds", contains(1, 2)))
                .andExpect(jsonPath("$.teamMemberIds", hasSize(2)))
                .andExpect(jsonPath("$.teamMemberIds", contains(1, 2)));
    }

    @Test
    void testDeleteMoment() throws Exception {
        Long momentId = 1L;
        mockMvc.perform(delete("/moment/{id}", momentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllMoments() throws Exception {
        List<MomentDto> momentDtos = Arrays.asList(momentDto);
        when(momentService.getAllMoments()).thenReturn(momentDtos);

        mockMvc.perform(get("/moment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Moment"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].imageId").value("imageId"))
                .andExpect(jsonPath("$[0].projectIds", hasSize(2)))
                .andExpect(jsonPath("$[0].projectIds", contains(1, 2)))
                .andExpect(jsonPath("$[0].teamMemberIds", hasSize(2)))
                .andExpect(jsonPath("$[0].teamMemberIds", contains(1, 2)));
    }

    @Test
    void testGetMomentById() throws Exception {
        Long momentId = 1L;
        when(momentService.getMomentById(momentId)).thenReturn(momentDto);
        mockMvc.perform(get("/moment/{id}", momentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Moment"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void testFilterMoments() throws Exception {
        List<MomentDto> momentDtos = Arrays.asList(momentDto);
        LocalDateTime now = LocalDateTime.now();
        MomentFilterDto filterDto = MomentFilterDto.builder()
                .fromDate(now.minusDays(1))
                .toDate(now.plusDays(1))
                .build();
        when(momentService.filterMomentsByDate(any(MomentFilterDto.class))).thenReturn(momentDtos);

        mockMvc.perform(post("/moment/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Moment"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].imageId").value("imageId"))
                .andExpect(jsonPath("$[0].projectIds", hasSize(2)))
                .andExpect(jsonPath("$[0].projectIds", contains(1, 2)))
                .andExpect(jsonPath("$[0].teamMemberIds", hasSize(2)))
                .andExpect(jsonPath("$[0].teamMemberIds", contains(1, 2)));
    }
}
