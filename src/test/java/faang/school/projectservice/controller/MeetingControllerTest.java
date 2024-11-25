package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {MeetingController.class, MeetingService.class})
public class MeetingControllerTest {

    private static final String BASE_URL = "/api/v1/meetings";
    private static final String CREATE_URL = "/meeting";
    private static final String UPDATE_URL = "/{meetId}";
    private static final String DELETE_URL = "/{projectId}/{userId}/{meetId}";
    private static final String FILTER_URL = "/filters";
    private static final String GET_BY_ID_URL = "/meeting/{meetId}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingService meetingService;

    private MeetDto meetDto;

    @BeforeEach
    void setup() {
        meetDto = MeetDto.builder()
                .id(1L)
                .title("Team meeting")
                .description("Meeting description")
                .projectId(100L)
                .creatorId(1L)
                .meetStatus(MeetStatus.COMPLETED)
                .build();
    }

    @Test
    void testCreateMeeting() throws Exception {
        when(meetingService.createMeeting(any(MeetDto.class))).thenReturn(meetDto);

        mockMvc.perform(post(BASE_URL + CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(meetDto.getTitle()))
                .andExpect(jsonPath("$.creatorId").value(meetDto.getCreatorId()))
                .andExpect(jsonPath("$.description").value(meetDto.getDescription()));

        verify(meetingService, times(1)).createMeeting(any(MeetDto.class));
    }

    @Test
    void testUpdateMeeting() throws Exception {
        when(meetingService.updateMeeting(any(MeetDto.class), anyLong())).thenReturn(meetDto);

        mockMvc.perform(post(BASE_URL + UPDATE_URL, meetDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(meetDto.getTitle()))
                .andExpect(jsonPath("$.description").value(meetDto.getDescription()));

        verify(meetingService, times(1)).updateMeeting(any(MeetDto.class), anyLong());
    }

    @Test
    void testDeleteMeeting() throws Exception {
        doNothing().when(meetingService).deleteMeeting(anyLong(), anyLong(), anyLong());

        mockMvc.perform(delete(BASE_URL + DELETE_URL, 1L, 1L, 1L))
                .andExpect(status().isNoContent());

        verify(meetingService, times(1)).deleteMeeting(anyLong(), anyLong(), anyLong());
    }
    @Test
    void testFilterMeetings() throws Exception {
        when(meetingService.filterMeetings(any(MeetDto.class))).thenReturn(List.of(meetDto));

        mockMvc.perform(get(BASE_URL + FILTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(meetDto.getId()))
                .andExpect(jsonPath("$[0].creatorId").value(meetDto.getCreatorId()))
                .andExpect(jsonPath("$[0].title").value(meetDto.getTitle()))
                .andExpect(jsonPath("$[0].description").value(meetDto.getDescription()));

        verify(meetingService, times(1)).filterMeetings(any(MeetDto.class));
    }

    @Test
    void testGetAllMeetings() throws Exception {
        when(meetingService.getAllMeetings(anyLong())).thenReturn(List.of(meetDto));

        mockMvc.perform(get(BASE_URL)
                        .param("projectId", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value(meetDto.getTitle()))
                .andExpect(jsonPath("$[0].creatorId").value(meetDto.getCreatorId()));

        verify(meetingService, times(1)).getAllMeetings(anyLong());
    }

    @Test
    void testGetMeetingById() throws Exception {
        when(meetingService.getMeetingById(anyLong())).thenReturn(meetDto);

        mockMvc.perform(get(BASE_URL + GET_BY_ID_URL, meetDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(meetDto.getTitle()))
                .andExpect(jsonPath("$.creatorId").value(meetDto.getCreatorId()));

        verify(meetingService, times(1)).getMeetingById(anyLong());
    }
}
