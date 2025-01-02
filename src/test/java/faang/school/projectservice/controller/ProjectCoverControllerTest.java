package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.context.UserHeaderFilter;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.handler.ExceptionApiHandler;
import faang.school.projectservice.service.ProjectCoverService;
import faang.school.projectservice.service.s3.FileUploadConfig;
import faang.school.projectservice.utilities.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProjectCoverController.class)
@Import({ProjectCoverController.class,
        ExceptionApiHandler.class,
        UserContext.class,
        UserHeaderFilter.class,
        FileUploadConfig.class})
class ProjectCoverControllerTest {

    @MockBean
    ProjectCoverService projectCoverService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file;
    private final Long projectId = 1L;
    private final String key = "123456-0bcd-ef01-12345678";
    private final String mainPartUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECT_COVER;
    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(key,
                "test.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4, 5});
        resourceDto = ResourceDto.builder()
                .key(key)
                .build();
    }

    @Test
    void addResourceSuccessTest() throws Exception {
        when(projectCoverService.add(eq(projectId), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart(mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(key));
    }

    @Test
    @GetMapping
    void uploadSuccessTest() throws Exception {
        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        when(projectCoverService.upload(projectId)).thenReturn(inputStream);

        mockMvc.perform(MockMvcRequestBuilders.get(mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void deleteResourceSuccessTest() throws Exception {
        ResourceDto resourceDto = ResourceDto.builder()
                .key(key)
                .build();
        when(projectCoverService.delete(projectId)).thenReturn(resourceDto);
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(key));
    }
}