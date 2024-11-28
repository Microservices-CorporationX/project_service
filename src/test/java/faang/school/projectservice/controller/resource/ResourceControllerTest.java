package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.RequestDeleteResourceDto;
import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = ResourceController.class)
class ResourceControllerTest {
    private final static String URL_UPLOAD = "/api/v1/resources/{projectId}/upload";
    private final static String URL_DELETE = "/api/v1/resources/delete";
    private final static String URL_UPDATE = "/api/v1/resources/{resourceId}/update";
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @MockBean
    private ResourceService resourceService;
    @MockBean
    private UserContext userContext;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testUpload_Positive() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "TestName",
                "text/plain",
                "Test".getBytes()
        );
        long mockUserId = 1L;
        long projectId = 4L;
        List<ResponseResourceDto> mockResponseDtos = List.of(
                ResponseResourceDto.builder()
                        .id(1L)
                        .key("4project-4/TestName")
                        .build()
        );

        when(userContext.getUserId()).thenReturn(mockUserId);
        when(resourceService.uploadResources(anyList(), anyLong(), anyLong())).thenReturn(mockResponseDtos);

        mockMvc.perform(multipart(URL_UPLOAD, projectId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(mockResponseDtos)));
    }

    @Test
    void testDelete_Positive() throws Exception {
        long mockUserId = 1L;
        RequestDeleteResourceDto requestDto = RequestDeleteResourceDto.builder()
                .id(1L)
                .build();
        when(userContext.getUserId()).thenReturn(mockUserId);
        mockMvc.perform(MockMvcRequestBuilders.delete(URL_DELETE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdate_Positive() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "TestName",
                "text/plain",
                "Test".getBytes()
        );
        long mockUserId = 1L;
        long resourceId = 1L;
        ResponseResourceDto responseResourceDto = ResponseResourceDto.builder()
                .id(1L)
                .key("4project-4/TestName")
                .build();
        when(userContext.getUserId()).thenReturn(mockUserId);
        when(resourceService.updateResource(any(MultipartFile.class), anyLong(), anyLong()))
                .thenReturn(responseResourceDto);

        mockMvc.perform(multipart(URL_UPDATE, resourceId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseResourceDto)));
    }
}