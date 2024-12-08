package faang.school.projectservice.controller.internship;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.config.web.WebConfig;
import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(MockitoExtension.class)
@TestPropertySource("${api.version}")
public class InternshipControllerTest {

    @Mock
    private InternshipService internshipService;

    @InjectMocks
    private InternshipController internshipController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(internshipController)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateInternship() throws Exception {
        InternshipDto internshipDto = prepareInternshipDto(InternshipStatus.IN_PROGRESS);
        String json = objectMapper.writeValueAsString(internshipDto);

        when(internshipService.createInternship(internshipDto)).thenReturn(internshipDto);

        mockMvc.perform(post("/internships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.internIds", hasSize(2)))
                .andExpect(jsonPath("$.mentorId").value(4))
                .andExpect(jsonPath("$.projectId").value(5))
                .andExpect(jsonPath("$.internshipStatus", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.startDate").value("2024-10-01T14:30"))
                .andExpect(jsonPath("$.endDate").value("2024-12-01T14:30"));
    }

    @Test
    void testUpdateInternship() throws Exception {
        InternshipDto internshipDto = prepareInternshipDto(InternshipStatus.IN_PROGRESS);
        String json = objectMapper.writeValueAsString(internshipDto);

        when(internshipService.updateInternship(internshipDto)).thenReturn(internshipDto);

        mockMvc.perform(put("/internships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.internIds", hasSize(2)))
                .andExpect(jsonPath("$.mentorId").value(4))
                .andExpect(jsonPath("$.projectId").value(5))
                .andExpect(jsonPath("$.internshipStatus", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.startDate").value("2024-10-01T14:30"))
                .andExpect(jsonPath("$.endDate").value("2024-12-01T14:30"));
    }

    @Test
    void testGetAllInternships() throws Exception {
        List<InternshipDto> allInternshipsDto = List.of(
                prepareInternshipDto(InternshipStatus.IN_PROGRESS),
                prepareInternshipDto(InternshipStatus.IN_PROGRESS));
        String json = objectMapper.writeValueAsString(allInternshipsDto);

        when(internshipService.getInternships()).thenReturn(allInternshipsDto);

        mockMvc.perform(get("/internships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].internIds", hasSize(2)))
                .andExpect(jsonPath("$[0].mentorId").value(4))
                .andExpect(jsonPath("$[0].projectId").value(5))
                .andExpect(jsonPath("$[0].internshipStatus").value(InternshipStatus.IN_PROGRESS.toString()))
                .andExpect(jsonPath("$[0].startDate").value("2024-10-01T14:30"))
                .andExpect(jsonPath("$[0].endDate").value("2024-12-01T14:30"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].internIds", hasSize(2)))
                .andExpect(jsonPath("$[1].mentorId").value(4))
                .andExpect(jsonPath("$[1].projectId").value(5))
                .andExpect(jsonPath("$[1].internshipStatus").value(InternshipStatus.IN_PROGRESS.toString()))
                .andExpect(jsonPath("$[1].startDate").value("2024-10-01T14:30"))
                .andExpect(jsonPath("$[1].endDate").value("2024-12-01T14:30"));
    }

    @Test
    void testGetAllInternshipsWithFilters() throws Exception {
        InternshipDto firstInternshipDto = prepareInternshipDto(InternshipStatus.COMPLETED);
        InternshipDto secondInternshipDto = prepareInternshipDto(InternshipStatus.COMPLETED);
        List<InternshipDto> allInternshipsDto = List.of(firstInternshipDto, secondInternshipDto);
        InternshipFilterDto filters = InternshipFilterDto.builder()
                .statusPattern(InternshipStatus.COMPLETED)
                .build();
        String json = objectMapper.writeValueAsString(filters);

        when(internshipService.getInternships(any(InternshipFilterDto.class))).thenReturn(allInternshipsDto);

        mockMvc.perform(get("/internships/filters", filters.getStatusPattern())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].internshipStatus").value(InternshipStatus.COMPLETED.toString()))
                .andExpect(jsonPath("$[1].internshipStatus").value(InternshipStatus.COMPLETED.toString()));
    }

    @Test
    void testGetInternshipById() throws Exception {
        InternshipDto internshipDto = prepareInternshipDto(InternshipStatus.IN_PROGRESS);
        String json = objectMapper.writeValueAsString(internshipDto);

        when(internshipService.getInternship(internshipDto.getId())).thenReturn(internshipDto);

        mockMvc.perform(get("/internships/{internshipId}", internshipDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.internIds", hasSize(2)))
                .andExpect(jsonPath("$.mentorId").value(4))
                .andExpect(jsonPath("$.projectId").value(5))
                .andExpect(jsonPath("$.internshipStatus", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.startDate").value("2024-10-01T14:30"))
                .andExpect(jsonPath("$.endDate").value("2024-12-01T14:30"));
    }

    private InternshipDto prepareInternshipDto(InternshipStatus internshipStatus) {
        return InternshipDto.builder()
                .id(3L)
                .name("Test")
                .description("Test")
                .internIds(List.of(1L, 2L))
                .mentorId(4L)
                .projectId(5L)
                .internshipStatus(internshipStatus)
                .startDate(LocalDateTime.of(2024, 10, 1, 14, 30))
                .endDate(LocalDateTime.of(2024, 12, 1, 14, 30))
                .build();
    }
}
