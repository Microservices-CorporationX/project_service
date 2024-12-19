package faang.school.projectservice.controller.google.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.service.google.GoogleCalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class GoogleCalendarControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @InjectMocks
    private GoogleCalendarController googleCalendarController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(googleCalendarController).build();
    }

    @Test
    public void testGetEvent() throws Exception {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(1L);

        when(googleCalendarService.getEvent(meetDto.getId())).thenReturn(meetDto);

        mockMvc.perform(get("/api/v1/projects/calendar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testGetEvents() throws Exception {
        MeetDto fisrtMeetDto = new MeetDto();
        fisrtMeetDto.setId(1L);

        MeetDto secondMeetDto = new MeetDto();
        secondMeetDto.setId(2L);

        MeetFilterDto filterDto = new MeetFilterDto();

        when(googleCalendarService.getEvents(1L, filterDto))
                .thenReturn(List.of(fisrtMeetDto, secondMeetDto));

        mockMvc.perform(post("/api/v1/projects/1/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    public void testCreateEvent() throws Exception {
        MeetDto meetDto = prepareData();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(googleCalendarService.createEvent(any())).thenReturn(meetDto);

        mockMvc.perform(post("/api/v1/projects/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        MeetDto meetDto = prepareData();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(googleCalendarService.updateEvent(any())).thenReturn(meetDto);

        mockMvc.perform(put("/api/v1/projects/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    private MeetDto prepareData() {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(1L);
        meetDto.setTitle("title");
        meetDto.setDescription("description");
        meetDto.setStartDateTime(LocalDateTime.now().plusHours(1));
        meetDto.setEndDateTime(LocalDateTime.now().plusHours(2));
        meetDto.setProjectId(1L);

        return meetDto;
    }
}
