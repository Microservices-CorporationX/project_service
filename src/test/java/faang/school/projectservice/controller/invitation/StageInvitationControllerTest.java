package faang.school.projectservice.controller.invitation;

import faang.school.projectservice.dto.invitation.RejectionReasonDTO;
import faang.school.projectservice.dto.invitation.StageInvitationDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.invitation.StageInvitationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
@DisplayName("StageInvitationController Tests")
class StageInvitationControllerTest {

    private static final String URL_SEND_INVITATION = "/stage-invitation";
    private static final String URL_ACCEPT_INVITATION = "/stage-invitation/{invitationId}/accept";
    private static final String URL_REJECT_INVITATION = "/stage-invitation/{invitationId}/reject";
    private static final String URL_GET_FILTERED_INVITATIONS = "/stage-invitation/filter";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StageInvitationService stageInvitationService;

    @Test
    @DisplayName("Тест успешного отправления приглашения")
    void testSendInvitation_SuccessfulCase() throws Exception {
        StageInvitationDTO inputDTO = StageInvitationDTO.builder()
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

        when(stageInvitationService.sendInvitation(inputDTO)).thenReturn(expectedDTO);

        mockMvc.perform(post(URL_SEND_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(inputDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)));
    }

    @ParameterizedTest
    @MethodSource("invalidSendInvitationDTO")
    @DisplayName("Тест с некорректными данными: обязательные поля отсутствуют")
    void testSendInvitation_MissingRequiredFields_ShouldReturnBadRequest(StageInvitationDTO inputDTO) throws Exception {
        mockMvc.perform(post(URL_SEND_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(inputDTO)))
            .andExpect(status().isBadRequest());
    }

    static Stream<StageInvitationDTO> invalidSendInvitationDTO() {
        return Stream.of(
            StageInvitationDTO.builder().stageId(2L).authorId(3L).status(StageInvitationStatus.PENDING).build(),
            StageInvitationDTO.builder().authorId(3L).invitedId(4L).status(StageInvitationStatus.PENDING).build(),
            StageInvitationDTO.builder().stageId(2L).invitedId(4L).status(StageInvitationStatus.PENDING).build()
        );
    }

    @Test
    @DisplayName("Тест успешного принятия приглашения")
    void testAcceptInvitation_SuccessfulAcceptance() throws Exception {
        Long invitationId = 1L;
        StageInvitationDTO expectedDTO = StageInvitationDTO.builder()
            .id(invitationId)
            .status(StageInvitationStatus.ACCEPTED)
            .build();

        when(stageInvitationService.acceptInvitation(invitationId)).thenReturn(expectedDTO);

        mockMvc.perform(patch(URL_ACCEPT_INVITATION, invitationId))
            .andExpect(status().isOk())
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)));
    }

    @Test
    @DisplayName("Тест отклонения без причины")
    void testRejectInvitation_MissingReason_ShouldReturnBadRequest() throws Exception {
        Long invitationId = 1L;
        RejectionReasonDTO emptyReasonDTO = new RejectionReasonDTO();

        mockMvc.perform(patch(URL_REJECT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(emptyReasonDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест успешного отклонения приглашения")
    void testRejectInvitation_SuccessfulRejection() throws Exception {
        Long invitationId = 1L;
        RejectionReasonDTO reasonDTO = new RejectionReasonDTO("Занят на других проектах");

        StageInvitationDTO expectedDTO = StageInvitationDTO.builder()
            .id(invitationId)
            .status(StageInvitationStatus.REJECTED)
            .description(reasonDTO.getRejectReason())
            .build();

        when(stageInvitationService.rejectInvitation(invitationId, reasonDTO.getRejectReason())).thenReturn(expectedDTO);

        mockMvc.perform(patch(URL_REJECT_INVITATION, invitationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(reasonDTO)))
            .andExpect(status().isOk())
            .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedDTO)));
    }

    @Test
    @DisplayName("Тест ошибки при некорректном JSON для фильтрации приглашений")
    void testGetFilteredInvitations_InvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{invalid}";

        mockMvc.perform(post(URL_GET_FILTERED_INVITATIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}
