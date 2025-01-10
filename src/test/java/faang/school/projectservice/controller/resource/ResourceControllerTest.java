package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.ResourceDtoStored;
import faang.school.projectservice.handler.ExceptionApiHandler;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.utilities.UrlUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@Import({ResourceController.class, ExceptionApiHandler.class})
public class ResourceControllerTest {
    private final static String mainUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.RESOURCE;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ResourceService resourceService;
    @MockBean
    private UserContext userContext;

    private MockMultipartFile file;

    private final long resourceId = 1L;
    private final long userId = 2L;
    private final long projectId = 3L;
    private static final String restParamName = "file";
    private static final String fileName = "test.jpeg";
    private final String fileKey = "file key";
    private final String errorMessage = "Error message";

    @BeforeEach
    public void setUp() {
        file = new MockMultipartFile(
                restParamName,
                fileName,
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3, 4}
        );
    }

    @Test
    public void downloadResourceSuccessTest() throws Exception {
        ResourceDtoStored resourceDtoStored = new ResourceDtoStored();
        resourceDtoStored.setId(resourceId);

        when(resourceService.downloadResource(resourceId)).thenReturn(resourceDtoStored);

        mockMvc.perform(get(mainUrl + "/" + resourceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(resourceService, times(1)).downloadResource(resourceId);
    }

    @Test
    public void downloadResourceFailTest() throws Exception {
        when(resourceService.downloadResource(resourceId)).thenThrow(new EntityNotFoundException(String.format(ResourceService.RESOURCE_NOT_FOUND_BY_ID, resourceId)));

        mockMvc.perform(get(mainUrl + "/" + resourceId))
                .andExpect(status().is4xxClientError());

        verify(resourceService, times(1)).downloadResource(resourceId);
    }

    @Test
    public void addResourceSuccessTest() throws Exception {
        ResourceDto resourceDto = getResourceDto();

        when(resourceService.addResource(eq(projectId), eq(userId), any(MultipartFile.class))).thenReturn(resourceDto);
        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(multipart(mainUrl + UrlUtils.ID + UrlUtils.ADD, projectId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceDto.getId()))
                .andExpect(jsonPath("$.name").value(resourceDto.getName()))
                .andExpect(jsonPath("$.key").value(resourceDto.getKey()));

        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void addResourceWithFileNullFailTest() throws Exception {
        mockMvc.perform(multipart(mainUrl + UrlUtils.ID + UrlUtils.ADD, projectId)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addResourceWithoutUserIdFailTest() throws Exception {
        when(userContext.getUserId()).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(multipart(mainUrl + UrlUtils.ID + UrlUtils.ADD, projectId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateResourceSuccessTest() throws Exception {
        ResourceDto resourceDto = getResourceDto();

        when(resourceService.updateResource(eq(resourceId), eq(userId), any(MultipartFile.class))).thenReturn(resourceDto);
        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(multipart(mainUrl + UrlUtils.ID, resourceId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceDto.getId()))
                .andExpect(jsonPath("$.name").value(resourceDto.getName()))
                .andExpect(jsonPath("$.key").value(resourceDto.getKey()));

        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void updateResourceWithFileNullFailTest() throws Exception {
        mockMvc.perform(multipart(mainUrl + UrlUtils.ID, resourceId)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateResourceWithoutUserIdFailTest() throws Exception {
        when(userContext.getUserId()).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(multipart(mainUrl + UrlUtils.ID, resourceId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteResourceSuccessTest() throws Exception {
        when(resourceService.deleteResource(resourceId, userId)).thenReturn(resourceId);
        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete(mainUrl + "/" + resourceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1L));

        verify(resourceService, times(1)).deleteResource(resourceId, userId);
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void deleteResourceWithoutUserIdFailTest() throws Exception {
        when(userContext.getUserId()).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(delete(mainUrl + "/" + resourceId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(userContext, times(1)).getUserId();
    }

    private ResourceDto getResourceDto() {

        LocalDateTime now = LocalDateTime.now();
        List<TeamRole> allowedRoles = List.of(TeamRole.OWNER);
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(resourceId);
        resourceDto.setName(fileName);
        resourceDto.setKey(fileKey);
        resourceDto.setSize(BigInteger.valueOf(1L));
        resourceDto.setType(ResourceType.PDF);
        resourceDto.setStatus(ResourceStatus.ACTIVE);
        resourceDto.setAllowedRoles(allowedRoles);
        resourceDto.setCreatedAt(now);
        resourceDto.setUpdatedAt(now);
        return resourceDto;
    }
}
