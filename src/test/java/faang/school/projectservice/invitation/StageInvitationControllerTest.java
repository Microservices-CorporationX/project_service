package faang.school.projectservice.invitation;

import faang.school.projectservice.controller.invitation.StageInvitationController;
import faang.school.projectservice.dto.invitation.RejectionReasonDTO;
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

    private static final String URL_SEND_INVITATION = "/stage-invitations";
    private static final String URL_ACCEPT_INVITATION = "/stage-invitations/{invitationId}/accept";
    private static final String URL_REJECT_INVITATION = "/stage-invitations/{invitationId}/reject";
    private static final String URL_GET_ALL_INVITATIONS = "/stage-invitations/users/{userId}/all-invitations";




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

        mockMvc.perform(post(URL_SEND_INVITATION)
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
        mockMvc.perform(post(URL_SEND_INVITATION)
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

        mockMvc.perform(patch(URL_ACCEPT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)))
            .andExpect(status().isOk());
    }


    //не могу понять что не так


    @Test
    @DisplayName("Тест успешного отклонения приглашения")
    void positiveTestRejectInvitation() throws Exception {

        Long invitationId = 1L;
        RejectionReasonDTO rejectionReasonDTO = RejectionReasonDTO.builder()
            .reason("Not interested")
            .build();

        StageInvitationDTO expectedDTO = StageInvitationDTO.builder()
            .id(invitationId)
            .stageId(2L)
            .authorId(3L)
            .inviteeId(4L)
            .status(StageInvitationStatus.REJECTED)
            .rejectionReason("Not interested")
            .build();

        when(stageInvitationService.rejectInvitation(invitationId, rejectionReasonDTO.getReason()))
            .thenReturn(expectedDTO);

        mockMvc.perform(patch(URL_REJECT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(rejectionReasonDTO)))
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

        mockMvc.perform(get(URL_GET_ALL_INVITATIONS, userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedInvitations)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест ошибки при принятии приглашения")
    void negativeTestAcceptInvitation() throws Exception {
        Long invitationId = 1L;

        when(stageInvitationService.acceptInvitation(invitationId))
            .thenThrow(new Exception("Invitation not found"));

        mockMvc.perform(patch(URL_ACCEPT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Invitation not found"));
    }

    @Test
    @DisplayName("Тест получения приглашений с фильтрацией по статусу")
    void testGetAllInvitationsWithStatusFilter() throws Exception {
        Long userId = 1L;
        String status = "PENDING";

        List<StageInvitationDTO> filteredInvitations = List.of(
            StageInvitationDTO.builder()
                .id(1L)
                .stageId(2L)
                .authorId(3L)
                .inviteeId(4L)
                .status(StageInvitationStatus.PENDING)
                .rejectionReason(null)
                .build()
        );

        when(stageInvitationService.getAllInvitationsForUser(userId,
            StageInvitationStatus.valueOf(status),
            null)).thenReturn(filteredInvitations);

        mockMvc.perform(get(URL_GET_ALL_INVITATIONS, userId)
                .queryParam("status", status)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(filteredInvitations)));
    }

    @Test
    @DisplayName("Тест ошибки при отсутствии причины отклонения")
    void negativeTestRejectInvitationWithEmptyReason() throws Exception {
        Long invitationId = 1L;
        RejectionReasonDTO invalidDTO = RejectionReasonDTO.builder().reason("").build();

        mockMvc.perform(patch(URL_REJECT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invalidDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест ошибки при некорректном JSON для отправки приглашения")
    void negativeTestSendInvitationWithInvalidJson() throws Exception {
        String invalidJson = "{invalid}";

        mockMvc.perform(post(URL_SEND_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}
