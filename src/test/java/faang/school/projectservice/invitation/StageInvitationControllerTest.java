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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {StageInvitationController.class, StageInvitationService.class})
public class StageInvitationControllerTest {

    private static final String URL_SEND_INVITATION = "/stageInvitation";
    private static final String URL_ACCEPT_INVITATION = "/stageInvitation/{invitationId}/accept";
    private static final String URL_REJECT_INVITATION = "/stageInvitation/{invitationId}/reject";
    private static final String URL_GET_FILTERED_INVITATIONS = "/stageInvitation/filter";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StageInvitationService stageInvitationService;

    @Test
    @DisplayName("Тест успешного отправления приглашения")
    void positiveTestSendInvitation() throws Exception {

        StageInvitationDTO invitationDTO = StageInvitationDTO.builder()
            .id(1L)
            .stageId(2L)
            .authorId(3L)
            .invitedId(4L)
            .status(StageInvitationStatus.PENDING)
            .description("reason")
            .build();

        StageInvitationDTO expectedDTO = StageInvitationDTO.builder()
            .id(1L)
            .stageId(2L)
            .authorId(3L)
            .invitedId(4L)
            .status(StageInvitationStatus.PENDING)
            .description("reason")
            .build();

        when(stageInvitationService.sendInvitation(invitationDTO)).thenReturn(expectedDTO);

        mockMvc.perform(post(URL_SEND_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invitationDTO)))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)))
            .andExpect(status().isCreated());
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
            .invitedId(4L)
            .status(StageInvitationStatus.ACCEPTED)
            .description(null)
            .build();

        when(stageInvitationService.acceptInvitation(invitationId)).thenReturn(expectedDTO);

        mockMvc.perform(patch(URL_ACCEPT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)))
            .andExpect(status().isOk());
    }

//    @Test
//    @DisplayName("Тест ошибки при принятии приглашения")
//    void negativeTestAcceptInvitation() throws Exception {
//        Long invitationId = 1L;
//
//        when(stageInvitationService.acceptInvitation(invitationId))
//            .thenThrow(new Exception("Invitation not found"));
//
//        mockMvc.perform(patch(URL_ACCEPT_INVITATION, invitationId)
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNotFound())
//            .andExpect(content().string("Invitation not found"));
//    }

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
            .invitedId(4L)
            .status(StageInvitationStatus.REJECTED)
            .description("Not interested")
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
    @DisplayName("Тест ошибки при отклонении приглашения с пустой причиной")
    void negativeTestRejectInvitationWithEmptyReason() throws Exception {
        Long invitationId = 1L;
        RejectionReasonDTO invalidDTO = RejectionReasonDTO.builder().reason("").build();

        mockMvc.perform(patch(URL_REJECT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invalidDTO)))
            .andExpect(status().isBadRequest());
    }

//    @Test
//    @DisplayName("Тест фильтрации приглашений")
//    void positiveTestGetFilteredInvitations() throws Exception {
//        StageInvitationDTO filterDTO = StageInvitationDTO.builder()
//            .authorId(3L)
//            .status(StageInvitationStatus.PENDING)
//            .build();
//
//        List<StageInvitationDTO> filteredInvitations = List.of(
//            StageInvitationDTO.builder()
//                .id(1L)
//                .stageId(2L)
//                .authorId(3L)
//                .invitedId(4L)
//                .status(StageInvitationStatus.PENDING)
//                .description(null)
//                .build()
//        );
//
//        when(stageInvitationService.getFilteredInvitations(filterDTO)).thenReturn(filteredInvitations);
//
//        mockMvc.perform(post(URL_GET_FILTERED_INVITATIONS)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(OBJECT_MAPPER.writeValueAsString(filterDTO)))
//            .andExpect(status().isOk())
//            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(filteredInvitations)));
//    }

    @Test
    @DisplayName("Тест ошибки при некорректном JSON для фильтрации приглашений")
    void negativeTestGetFilteredInvitationsWithInvalidJson() throws Exception {
        String invalidJson = "{invalid}";

        mockMvc.perform(post(URL_GET_FILTERED_INVITATIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    static Stream<Object[]> invalidSendInvitationDTO() {
        return Stream.of(
            new Object[]{StageInvitationDTO.builder()
                .id(1L)
                .stageId(2L)
                .authorId(3L)
                .status(StageInvitationStatus.PENDING)
                .description("reason")
                .build()},
            new Object[]{StageInvitationDTO.builder()
                .id(1L)
                .stageId(2L)
                .invitedId(4L)
                .status(StageInvitationStatus.PENDING)
                .description("reason")
                .build()},
            new Object[]{StageInvitationDTO.builder()
                .id(1L)
                .authorId(3L)
                .invitedId(4L)
                .status(StageInvitationStatus.PENDING)
                .description("reason")
                .build()}
        );
    }
}
