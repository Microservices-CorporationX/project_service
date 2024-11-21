package faang.school.projectservice.controller.meet;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.meet.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(MeetController.class)
class MeetControllerTest {
    private final static String MEET_PATH = "/api/v1/meets";

    private final long creatorId = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetService meetService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserContext userContext;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Create meet - Success scenario")
    void create_shouldSaveMeetAndReturnDto() throws Exception {
        MeetRequestDto requestDto = MeetRequestDto.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .build();

        MeetResponseDto responseDto = MeetResponseDto.builder()
                .creatorId(creatorId)
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .build();

        when(meetService.createMeet(creatorId, requestDto)).thenReturn(responseDto);


        mockMvc.perform(post(MEET_PATH)
                        .header("x-user-id", creatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}