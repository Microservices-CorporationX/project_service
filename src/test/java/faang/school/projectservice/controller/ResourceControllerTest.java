package faang.school.projectservice.controller;

import faang.school.projectservice.service.ResourceService;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    private static final String URL = "/resources/{projectId}";

    @Mock
    private ResourceService resourceService;

    @Mock
    private Tika tika;

    @InjectMocks
    private ResourceController resourceController;

    private MockMvc mockMvc;

    private Long projectId;
    private Long userId;
    private MockMultipartFile mockFile;
    private String originalFileName;
    private String mimeType;
    private byte[] content;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
        projectId = 1L;
        userId = 1L;
        originalFileName = "cover.jpg";
        mimeType = "image/jpeg";
        content = new byte[]{1, 2, 3};

        mockFile = new MockMultipartFile(
                "file",
                originalFileName,
                mimeType,
                content
        );
    }

    @Test
    void testUploadProjectCoverImage() throws Exception {
        mockMvc.perform(multipart(URL, projectId)
                        .file(mockFile)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(
                        String.format("Project %d cover '%s' successfully uploaded by User %d",
                                projectId, originalFileName, userId)));

        verify(resourceService, times(1)).uploadProjectCover(mockFile, userId, projectId);
    }

    @Test
    void testDeleteProjectCoverImage() throws Exception {
        mockMvc.perform(delete(URL, projectId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(
                        String.format("Project %d cover successfully deleted by User %d", projectId, userId)));

        verify(resourceService, times(1)).deleteProjectCover(userId, projectId);
    }

    @Test
    void testDownloadProjectCoverImage() throws Exception {
        when(resourceService.downloadProjectCover(userId, projectId)).thenReturn(content);
        when(tika.detect(content)).thenReturn(mimeType);

        mockMvc.perform(get(URL, projectId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, mimeType));

        verify(resourceService, times(1)).downloadProjectCover(userId, projectId);
    }
}