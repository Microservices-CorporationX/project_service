package faang.school.projectservice.controller.stage_invitation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.StageInvitationController;
import faang.school.projectservice.dto.client.AcceptStageInvitation;
import faang.school.projectservice.dto.client.RejectStageInvitation;
import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.client.StageInvitationFilters;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class StageInvitationControllerTest {
    private MockMvc mockMvc;

    @Mock
    private StageInvitationService stageInvitationService;

    @InjectMocks
    private StageInvitationController stageInvitationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stageInvitationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendStageInvitation() throws Exception {
        StageInvitationDto requestDto = new StageInvitationDto();
        requestDto.setId(1L);
        requestDto.setStageId(1L);
        requestDto.setAuthorId(1L);
        requestDto.setInvitedId(1L);
        requestDto.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto responseDto = new StageInvitationDto();
        responseDto.setId(1L);
        responseDto.setStageId(1L);
        responseDto.setAuthorId(1L);
        responseDto.setInvitedId(1L);
        responseDto.setStatus(StageInvitationStatus.PENDING);

        when(stageInvitationService.sendStageInvitation(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/stage-invitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testAcceptStageInvitation() throws Exception {
        AcceptStageInvitation stageInvitation = new AcceptStageInvitation();
        stageInvitation.setId(1L);

        StageInvitationDto responseDto = new StageInvitationDto();
        responseDto.setId(1L);
        responseDto.setStatus(StageInvitationStatus.ACCEPTED);

        when(stageInvitationService.acceptStageInvitation(stageInvitation)).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(stageInvitation);

        mockMvc.perform(put("/api/v1/stage-invitation/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void testRejectStageInvitation() throws Exception {
        RejectStageInvitation rejectStageInvitation = new RejectStageInvitation();
        rejectStageInvitation.setId(1L);
        rejectStageInvitation.setDescription("Declined due to scheduling issues");

        StageInvitationDto responseDto = new StageInvitationDto();
        responseDto.setId(1L);
        responseDto.setStatus(StageInvitationStatus.REJECTED);

        when(stageInvitationService.rejectStageInvitation(rejectStageInvitation)).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(rejectStageInvitation);

        mockMvc.perform(put("/api/v1/stage-invitation/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void testFilterStageInvitation() throws Exception {
        StageInvitationFilters filters = new StageInvitationFilters();
        filters.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto firstResponseDto = new StageInvitationDto();
        firstResponseDto.setId(1L);
        firstResponseDto.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto secondResponseDto = new StageInvitationDto();
        secondResponseDto.setId(2L);
        secondResponseDto.setStatus(StageInvitationStatus.PENDING);

        when(stageInvitationService.filters(any(StageInvitationFilters.class)))
                .thenReturn(List.of(firstResponseDto, secondResponseDto));

        String requestJson = objectMapper.writeValueAsString(filters);

        mockMvc.perform(post("/api/v1/stage-invitation/filters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("PENDING"));
    }
}