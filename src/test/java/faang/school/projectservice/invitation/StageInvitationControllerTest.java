package faang.school.projectservice.invitation;

import faang.school.projectservice.controller.invitation.StageInvitationController;
import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.invitation.StageInvitationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {StageInvitationController.class, StageInvitationService.class})
public class StageInvitationControllerTest {


    private final static String POST_URL = "/stage-invitations";
    private final static String POST_URL_ACCEPT = "/stage-invitations/{invitationId}/accept";
    private final static String POST_URL_REJECT = "/stage-invitations/{invitationId}/reject";
    private final static String POST_URL_ALL = "/stage-invitations/users/{userId}/all-invitations";



    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StageInvitationService stageInvitationService;

    @Test
    void positiveTestSendInvitation() throws Exception{

        StageInvitationDTO invitationDTO = StageInvitationDTO.builder()
            .id(1L)
            .stageId(2L)
            .authorId(3L)
            .inviteeId(4L)
            .status(StageInvitationStatus.PENDING)
            .rejectionReason("reason")
            .build();

        StageInvitationDTO expectedDTO= StageInvitationDTO.builder()
            .id(1L)
            .stageId(2L)
            .authorId(3L)
            .inviteeId(4L)
            .status(StageInvitationStatus.PENDING)
            .rejectionReason("reason")
            .build();

        when(stageInvitationService.sendInvitation(invitationDTO)).thenReturn(expectedDTO);

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invitationDTO)))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)))
            .andExpect(status().isCreated());
    }

    static Stream<Object[]> invalidSendInvitationDTO() {
        return Stream.of(
            new Object[]{StageInvitationDTO.builder()
                .id(1L)
                .stageId(2L)
                .authorId(3L)
                .status(StageInvitationStatus.PENDING)
                .rejectionReason("reason")
                .build()},
            new Object[]{StageInvitationDTO.builder()
                .id(1L)
                .stageId(2L)
                .inviteeId(4L)
                .status(StageInvitationStatus.PENDING)
                .rejectionReason("reason")
                .build()},
            new Object[]{StageInvitationDTO.builder()
                .id(1L)
                .authorId(3L)
                .inviteeId(4L)
                .status(StageInvitationStatus.PENDING)
                .rejectionReason("reason")
                .build()}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidSendInvitationDTO")
    @DisplayName("Тест с некорректными данными: обязательные поля отсутствуют")
    void negativeSendInvitationNoRequestArgument(StageInvitationDTO invitationDTO) throws Exception {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invitationDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест успешного принятия приглашения")
    void positiveTestAcceptInvitation() throws Exception {
        Long invitationId = 1L;

        StageInvitationDTO expectedDTO = StageInvitationDTO.builder()
            .id(invitationId)
            .stageId(2L)
            .authorId(3L)
            .inviteeId(4L)
            .status(StageInvitationStatus.ACCEPTED)
            .rejectionReason(null)
            .build();

        when(stageInvitationService.acceptInvitation(invitationId)).thenReturn(expectedDTO);

        mockMvc.perform(patch(POST_URL_ACCEPT, invitationId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест успешного отклонения приглашения")
    void positiveTestRejectInvitation() throws Exception {
        Long invitationId = 1L;

        StageInvitationDTO rejectionDTO = StageInvitationDTO.builder()
            .rejectionReason("Some rejection reason")
            .build();

        StageInvitationDTO expectedDTO = StageInvitationDTO.builder()
            .id(invitationId)
            .stageId(2L)
            .authorId(3L)
            .inviteeId(4L)
            .status(StageInvitationStatus.REJECTED)
            .rejectionReason("Some rejection reason")
            .build();

        when(stageInvitationService.rejectInvitation(invitationId, rejectionDTO)).thenReturn(expectedDTO);

        mockMvc.perform(patch(POST_URL_REJECT, invitationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(rejectionDTO)))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест успешного получения всех приглашений для пользователя")
    void positiveTestGetAllInvitations() throws Exception {
        Long userId = 1L;

        List<StageInvitationDTO> expectedInvitations = List.of(
            StageInvitationDTO.builder()
                .id(1L)
                .stageId(2L)
                .authorId(3L)
                .inviteeId(4L)
                .status(StageInvitationStatus.PENDING)
                .rejectionReason(null)
                .build(),
            StageInvitationDTO.builder()
                .id(2L)
                .stageId(3L)
                .authorId(4L)
                .inviteeId(5L)
                .status(StageInvitationStatus.ACCEPTED)
                .rejectionReason(null)
                .build()
        );

        when(stageInvitationService.getAllInvitationsForUser(userId, null, null)).thenReturn(expectedInvitations);

        mockMvc.perform(get(POST_URL_ALL, userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedInvitations)))
            .andExpect(status().isOk());
    }
}
