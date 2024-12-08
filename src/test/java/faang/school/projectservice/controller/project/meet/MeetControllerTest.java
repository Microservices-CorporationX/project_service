package faang.school.projectservice.controller.project.meet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.service.project.meet.MeetService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MeetControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MeetService meetService;

    @InjectMocks
    private MeetController meetController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(meetController).build();
    }

    @Test
    public void testGetEvent() throws Exception {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(1L);

        when(meetService.getMeetByIdAndUserId(meetDto.getId())).thenReturn(meetDto);

        mockMvc.perform(get("/api/v1/projects/meets/1"))
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

        when(meetService.getMeets(1L, filterDto))
                .thenReturn(List.of(fisrtMeetDto, secondMeetDto));

        mockMvc.perform(post("/api/v1/projects/1/meets")
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

        when(meetService.createMeet(any())).thenReturn(meetDto);

        mockMvc.perform(post("/api/v1/projects/meets/create")
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

        when(meetService.updateMeet(any())).thenReturn(meetDto);

        mockMvc.perform(put("/api/v1/projects/meets/update")
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
