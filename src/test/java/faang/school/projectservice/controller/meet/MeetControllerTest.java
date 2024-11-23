package faang.school.projectservice.controller.meet;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.ExternalServiceException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.handler.GlobalExceptionHandler;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.meet.MeetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetControllerTest.class)
@Import(GlobalExceptionHandler.class)
@ContextConfiguration(classes = {MeetController.class})
public class MeetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetService meetService;

    @MockBean
    private UserContext userContext;

    @Test
    void testCreateMeetingSuccessful() throws Exception {
        MeetDto meetDto = createMeetDto(null, "Planning", "The next sprint planning",
                null, 1L, 1L, List.of(1L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetService.createMeeting(meetDto)).thenReturn(meetDto);

        mockMvc.perform(post("/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(meetDto)));

        verify(meetService, times(1)).createMeeting(meetDto);
    }

    @Test
    void testCreateMeetingWithFailedInterServiceCommunication() throws Exception {
        MeetDto meetDto = createMeetDto(null, "Planning", "The next sprint planning",
                null, 1L, 1L, List.of(1L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetService.createMeeting(meetDto))
                .thenThrow(new ExternalServiceException("Failed to communicate with User Service. Please try again later."));

        mockMvc.perform(post("/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Interaction failure"))
                .andExpect(jsonPath("$.message").value("Failed to communicate with User Service. Please try again later."));

        verify(meetService, times(1)).createMeeting(meetDto);
    }

    @Test
    void testUpdateMeetingSuccessful() throws Exception {
        Long meetId = 1L;
        Long userId = 1L;
        MeetDto meetDto = createMeetDto(1L, "Planning", "The next sprint planning", MeetStatus.PENDING,
                1L, 1L, List.of(1L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetService.updateMeeting(meetId, userId, meetDto)).thenReturn(meetDto);

        mockMvc.perform(put("/meetings/{meetId}", meetId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(meetDto)));

        verify(meetService, times(1)).updateMeeting(meetId, userId, meetDto);
    }

    @Test
    void testUpdateMeetingThrowsUnauthorizedAccessException() throws Exception {
        Long meetId = 1L;
        Long userId = 2L;
        MeetDto meetDto = createMeetDto(1L, "Planning", "The next sprint planning", MeetStatus.PENDING,
                1L, 1L, List.of(1L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetService.updateMeeting(meetId, userId, meetDto))
                .thenThrow(new UnauthorizedAccessException("Only the creator can update the meeting"));

        mockMvc.perform(put("/meetings/{meetId}", meetId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized access"))
                .andExpect(jsonPath("$.message").value("Only the creator can update the meeting"));

        verify(meetService, times(1)).updateMeeting(meetId, userId, meetDto);
    }

    @Test
    void testDeleteMeetingSuccessful() throws Exception {
        Long meetId = 1L;
        Long userId = 1L;

        doNothing().when(meetService).deleteMeeting(meetId, userId);

        mockMvc.perform(delete("/meetings/{meetId}", meetId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());

        verify(meetService, times(1)).deleteMeeting(meetId, userId);
    }

    @Test
    void testDeleteMeetingThrowsUnauthorizedAccessException() throws Exception {
        Long meetId = 1L;
        Long userId = 1L;

        doThrow(new UnauthorizedAccessException("Only the creator can delete the meeting"))
                .when(meetService).deleteMeeting(meetId, userId);

        mockMvc.perform(delete("/meetings/{meetId}", meetId)
                        .header("x-user-id", userId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized access"))
                .andExpect(jsonPath("$.message").value("Only the creator can delete the meeting"));

        verify(meetService, times(1)).deleteMeeting(meetId, userId);
    }

    @Test
    void testGetMeetingsByProjectFilteredByDateOrTitleSuccessful() throws Exception {
        Long projectId = 1L;
        String title = "Planning";
        LocalDateTime dateFrom = LocalDateTime.of(2024, 11, 12, 10, 30);
        List<MeetDto> meetings = List.of(
                createMeetDto(1L, "Planning", "The next sprint planning", MeetStatus.PENDING,
                1L, 1L, List.of(1L), LocalDateTime.of(2024, 12, 12, 10, 30))
        );

        when(meetService.getMeetingsByProjectFilteredByDateOrTitle(projectId, title, dateFrom, null))
                .thenReturn(meetings);

        mockMvc.perform(get("/meetings/projects/{projectId}", projectId)
                        .param("title", title)
                        .param("dateFrom", dateFrom.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(meetings)));

        verify(meetService, times(1)).getMeetingsByProjectFilteredByDateOrTitle(projectId, title, dateFrom, null);
    }

    @Test
    void testGetAllMeetingsSuccessful() throws Exception {
        List<MeetDto> meetings = new ArrayList<>();

        when(meetService.getAllMeetings()).thenReturn(meetings);

        mockMvc.perform(get("/meetings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(meetings)));

        verify(meetService, times(1)).getAllMeetings();
    }

    @Test
    void testGetMeetingByIdSuccessful() throws Exception {
        Long meetId = 1L;
        MeetDto meetDto = createMeetDto(1L, "Planning", "The next sprint planning", MeetStatus.PENDING,
                1L, 1L, List.of(1L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetService.getMeetingById(meetId)).thenReturn(meetDto);

        mockMvc.perform(get("/meetings/{meetId}", meetId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(meetDto)));

        verify(meetService, times(1)).getMeetingById(meetId);
    }

    private MeetDto createMeetDto(Long id, String title, String description, MeetStatus status, Long creatorId,
                                  Long projectId, List<Long> userIds, LocalDateTime meetDate) {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(id);
        meetDto.setTitle(title);
        meetDto.setDescription(description);
        meetDto.setStatus(status);
        meetDto.setCreatorId(creatorId);
        meetDto.setProjectId(projectId);
        meetDto.setUserIds(userIds);
        meetDto.setMeetDate(meetDate);
        return meetDto;
    }
}
