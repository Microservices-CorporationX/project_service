package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.exception.GlobalExceptionHandler;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {ResourceController.class})
class ResourceControllerTest {

    private static final String BASE_URL = "/resources";
    private static final String UPLOAD_SINGLE_RESOURCE_URL = "/{projectId}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    ResourceResponseDto resourceResponseDto;
    MockMultipartFile file;
    Long projectId;
    Long userId;
    @Autowired
    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        resourceResponseDto = mockResourceResponseDto();
        file = mockMultipartFile();
        projectId = 1L;
        userId = 2L;
    }

    @Test
    @DisplayName("File upload success")
    void uploadFile_ValidFileAndParams_Success() throws Exception {
        when(resourceService.uploadResource(projectId, userId, file)).thenReturn(resourceResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(6L))
                .andExpect(jsonPath("$.message").value(String.format("File %s uploaded successfully", resourceResponseDto.getName())));
    }

    @Test
    @DisplayName("File upload fail: negative project id")
    void uploadFile_NegativeProjectId_FailBadRequest() throws Exception {
        projectId = -1L;

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Project id must be a positive integer")));
    }

    @Test
    @DisplayName("File upload fail: null project id")
    void uploadFile_NullProjectId_FailNotFound() throws Exception {
        projectId = null;

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("File upload fail: user id not provided")
    void uploadFile_UserIdNotProvided_FailBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("Parameter cannot be null"))
                .andExpect(content().string(containsString("Parameter cannot be null")));
    }

    @Test
    @DisplayName("File upload fail: negative user id")
    void uploadFile_NegativeUserId_FailBadRequest() throws Exception {
        userId = -1L;

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .file(file)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User id must be a positive integer")));
    }

    @Test
    @DisplayName("File upload fail: file not provided")
    void uploadFile_FileNotProvided_FailBadRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL + UPLOAD_SINGLE_RESOURCE_URL, projectId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.file").value("Value not provided"))
                .andExpect(content().string(containsString("Value not provided")));
    }

    private ResourceResponseDto mockResourceResponseDto() {
        return ResourceResponseDto.builder()
                .id(6L)
                .name("file")
                .key("project-folder/text.txt")
                .size(BigInteger.valueOf(1048576))
                .type(ResourceType.TEXT)
                .createdById(2L)
                .updatedById(3L)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2023, 10, 2, 14, 30))
                .projectId(1L)
                .build();
    }

    private MockMultipartFile mockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes()
        );
    }
}