package faang.school.projectservice.controller.stage_invitation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {StageInvitationController.class})
public class StageInvitationControllerTest {
    private final static String CREATE_URL ="/stageinvitation";
    private final static String ACCEPTINVITATION_URL = "/stageinvitation/accept-invitation";
    private final static String REGECT_INVITATION_URL = "/stageinvitation/reject-invitation";
    private final static String GET_FILTERS = "/stageinvitation/filter-invitation";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StageInvitationService service;

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private StageInvitationController stageInvitationController;

    @Test
    void positiveTestForCreate() throws Exception {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                                                .id(1L)
                                                .status(StageInvitationStatus.PENDING)
                                                .stageId(1L)
                                                .authorId(1L)
                                                .invitedId(1L)
                                                .build();

        when(service.create(any())).thenReturn(stageInvitationDto);

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(stageInvitationDto)))
                .andExpect(content().json(objectMapper.writeValueAsString(stageInvitationDto)))
                .andExpect(status().isOk());
    }

    static Stream<Object> invalidRequestDto() {
        return Stream.of(
                new Object[]{StageInvitationDto.builder()
                        .status(StageInvitationStatus.PENDING)
                        .stageId(1L)
                        .authorId(1L)
                        .invitedId(1L)
                        .build()
                },
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .status(StageInvitationStatus.PENDING)
                        .authorId(1L)
                        .invitedId(1L)
                        .build()
                },
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .status(StageInvitationStatus.PENDING)
                        .stageId(1L)
                        .invitedId(1L)
                        .build()
                },
                new Object[]{StageInvitationDto.builder()
                        .id(1L)
                        .status(StageInvitationStatus.PENDING)
                        .stageId(1L)
                        .authorId(1L)
                        .build()
                }
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequestDto")
    void negativeTestForCreate(StageInvitationDto stageInvitationDto) throws Exception {
        mockMvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(stageInvitationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void positiveTestForAcceptInvitation() throws Exception {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.ACCEPTED)
                .stageId(1L)
                .authorId(2L)
                .invitedId(1L)
                .build();

        when(service.acceptInvitation(1L, 1L)).thenReturn(stageInvitationDto);

        mockMvc.perform(post(ACCEPTINVITATION_URL)
                        .param("invitationId", "1")
                        .param("invitedId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageInvitationDto)));
    }

    @Test
    void negativeTestForAcceptInvitation() throws Exception {
        mockMvc.perform(post(ACCEPTINVITATION_URL)
                        .param("invitedId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(ACCEPTINVITATION_URL)
                        .param("invitationId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void positiveTestForRejectInvitation() throws Exception {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.REJECTED)
                .stageId(1L)
                .authorId(2L)
                .invitedId(1L)
                .build();

        when(service.rejectInvitation(1L, 1L, "description")).thenReturn(stageInvitationDto);

        mockMvc.perform(post(REGECT_INVITATION_URL)
                        .param("invitationId", "1")
                        .param("invitedId", "1")
                        .param("rejectDescription", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageInvitationDto)));
    }

    @Test
    void negativeTestForRejectInvitation() throws Exception {
        mockMvc.perform(post(REGECT_INVITATION_URL)
                        .param("invitedId", "1")
                        .param("rejectDescription", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(REGECT_INVITATION_URL)
                        .param("invitationId", "1")
                        .param("rejectDescription", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(REGECT_INVITATION_URL)
                        .param("invitationId", "1")
                        .param("invitedId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void positiveTestForCheckAllInvitation() throws Exception {
        StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.PENDING)
                .stageId(1L)
                .authorId(2L)
                .invitedId(1L)
                .build();

        StageInvitationFilterDto stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .status(StageInvitationStatus.PENDING)
                .stage(new Stage())
                .author(new TeamMember())
                .invited(new TeamMember())
                .build();

        when(service.checkAllInvitation(eq(1L), any(StageInvitationFilterDto.class)))
                .thenReturn(List.of(stageInvitationDto));

        mockMvc.perform(get(GET_FILTERS)
                        .param("invitedId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(stageInvitationFilterDto)))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(stageInvitationDto))))
                .andExpect(status().isOk());
    }
}
