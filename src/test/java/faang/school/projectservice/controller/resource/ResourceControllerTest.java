package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {ResourceController.class, ResourceService.class})
class ResourceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserContext userContext;
    @MockBean
    private ResourceService resourceService;

    @Test
    void uploadResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test content".getBytes());
        ResourceDto resourceDto = ResourceDto.builder()
                .id(1L)
                .name("test.png")
                .size(BigInteger.valueOf(1024))
                .createdBy(100L)
                .createdAt("2024-11-27T12:00:00")
                .build();

        when(resourceService.uploadResource(anyLong(), anyLong(), any(MultipartFile.class))).thenReturn(resourceDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/resources/projects/{projectId}", 1L)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(resourceDto.name()));

        verify(resourceService).uploadResource(eq(1L), anyLong(), any(MultipartFile.class));
    }

    @Test
    void downloadResource() throws Exception {
        Long resourceId = 1L;
        InputStream resourceStream = new ByteArrayInputStream("test content".getBytes());

        when(resourceService.downloadResource(resourceId)).thenReturn(resourceStream);

        mockMvc.perform(get("/resources/{resourceId}", resourceId)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().bytes("test content".getBytes()));

        verify(resourceService).downloadResource(resourceId);
    }

    @Test
    void updateResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "updated.png", "image/png", "updated content".getBytes());
        Long resourceId = 1L;
        ResourceDto updatedResourceDto = ResourceDto.builder()
                .id(1L)
                .name("test.png")
                .size(BigInteger.valueOf(1024))
                .createdBy(100L)
                .createdAt("2024-11-27T12:00:00")
                .build();

        when(userContext.getUserId()).thenReturn(100L);
        when(resourceService.updateResource(anyLong(), eq(resourceId), any(MultipartFile.class))).thenReturn(updatedResourceDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/resources/{resourceId}", resourceId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedResourceDto.name()));

        verify(resourceService).updateResource(eq(100L), eq(resourceId), any(MultipartFile.class));
    }

    @Test
    void deleteResource() throws Exception {
        Long resourceId = 1L;

        when(userContext.getUserId()).thenReturn(100L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/resources/{resourceId}", resourceId))
                .andExpect(status().isOk());

        verify(resourceService).deleteResource(resourceId, 100L);
    }
}