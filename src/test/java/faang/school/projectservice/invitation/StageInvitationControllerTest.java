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

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {StageInvitationController.class, StageInvitationService.class})
public class StageInvitationControllerTest {


    private final static String POST_URL = "/stage-invitations";

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
}
