package faang.school.projectservice.controller.momentController;

import faang.school.projectservice.dto.momentDto.MomentDto;
import faang.school.projectservice.service.momentService.MomentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {MomentController.class, MomentService.class})
public class MomentControllerTest {
    private final static String POST_URL = "/moments";
    private final static String GET_URL = "/moments";
    private final static String GET_ID_URL = "/moments/{id}";
    private final static String PATCH_URL = "/moments";
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MomentService momentService;

    @ParameterizedTest
    @MethodSource("nonValidMomentDtos")
    void testCreateMomentWhenArgsNotValid(MomentDto momentDto) throws Exception {
        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(momentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMomentWhenAllSuccess() throws Exception {
        MomentDto momentDto = createMomentDto();
        when(momentService.create(any(MomentDto.class))).thenReturn(momentDto);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(momentDto)))
                        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(momentDto)))
                        .andExpect(status().isCreated());
    }

    @Test
    void testUpdateMoment() throws Exception {
        MomentDto momentDto = createMomentDto();

        MomentDto mockMomentDto = createMomentDto();
        mockMomentDto.setProjectIds(List.of(1L, 2L, 3L, 4L));
        mockMomentDto.setUserIds(List.of(1L, 2L, 3L));
        when(momentService.update(anyLong(), anyLong(), anyLong())).thenReturn(mockMomentDto);

        mockMvc.perform(patch(PATCH_URL)
                        .param("id", "1")
                        .param("userId", "2")
                        .param("projectId", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(momentDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(mockMomentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testgetMomentsSuccess() throws Exception {
        MomentDto momentDto = createMomentDto();

        MomentDto mockMomentDto = createMomentDto();
        when(momentService.getMoments()).thenReturn(List.of(momentDto));

        mockMvc.perform(get(GET_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(mockMomentDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(mockMomentDto))))
                .andExpect(status().isOk());
    }

    @Test
    void testgetMomentByIdSuccess() throws Exception {
        MomentDto momentDto = createMomentDto();
        MomentDto mockMomentDto = createMomentDto();
        when(momentService.getMomentById(momentDto.getId())).thenReturn(mockMomentDto);

        mockMvc.perform(get(GET_ID_URL, momentDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(mockMomentDto)))
                        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(mockMomentDto)))
                        .andExpect(status().isOk());
    }

    private MomentDto createMomentDto() {
        return MomentDto.builder()
                .id(1L)
                .name("Moment")
                .description("Perhaps, best moments")
                .date("2024-01-25 12:00:23")
                .projectIds(List.of(1L, 2L, 3L))
                .userIds(List.of(1L, 2L))
                .createdAt("2024-01-24 12:00:23")
                .updatedAt("2024-01-24 12:00:23")
                .createdBy(1L).build();
    }

    private static Stream<Object[]> nonValidMomentDtos() {
        return Stream.of(
                new Object[]{
                        MomentDto.builder()
                                .name("Moment")
                                .description("Perhaps, best moments")
                                .date("2024-01-24 12:00:23")
                                .projectIds(List.of(1L, 2L, 3L))
                                .userIds(List.of(1L, 2L))
                                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .createdBy(1L).build()
                },
                new Object[]{
                        MomentDto.builder()
                                .id(1L)
                                .description("Perhaps, best moments")
                                .date("2024-01-24 12:00:23")
                                .projectIds(List.of(1L, 2L, 3L))
                                .userIds(List.of(1L, 2L))
                                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .createdBy(1L).build()
                },
                new Object[]{
                        MomentDto.builder()
                                .id(1L)
                                .name("Moment")
                                .date("2024-01-24 12:00:23")
                                .projectIds(List.of(1L, 2L, 3L))
                                .userIds(List.of(1L, 2L))
                                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .createdBy(1L).build()
                },
                new Object[]{
                        MomentDto.builder()
                                .id(1L)
                                .name("Moment")
                                .description("Perhaps, best moments")
                                .date("2024-01-24 12:00:23")
                                .userIds(List.of(1L, 2L))
                                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .createdBy(1L).build()
                },
                new Object[]{
                        MomentDto.builder()
                                .id(1L)
                                .name("Moment")
                                .description("Perhaps, best moments")
                                .date("2024-01-24 12:00:23")
                                .projectIds(List.of(1L, 2L, 3L))
                                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .updatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .createdBy(1L).build()
                }
        );
    }
}
