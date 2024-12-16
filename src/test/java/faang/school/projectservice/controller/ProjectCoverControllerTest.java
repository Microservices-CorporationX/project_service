package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.ProjectCoverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectCoverControllerTest {

    @Mock
    ProjectCoverService projectCoverService;
    @Mock
    MultipartFile file;

    @InjectMocks
    ProjectCoverController projectCoverController;

    private final Long projectId = 1L;
    private final String key = "123456-0bcd-ef01-12345678";

    @Test
    void testAddResourceSuccess() {
        ResourceDto resourceDto = new ResourceDto(key);
        when(projectCoverService.add(projectId, file)).thenReturn(resourceDto);

        ResourceDto resourceDtoResult = projectCoverController.addResource(projectId, file);
        assertEquals(resourceDto, resourceDtoResult);
        verify(projectCoverService, times(1)).add(anyLong(), any());
    }

    @Test
    void testUploadResourceSuccess() {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5};
        InputStream inputStream = new ByteArrayInputStream(bytes);
        when(projectCoverService.upload(projectId)).thenReturn(inputStream);

        ResponseEntity<byte[]> responseEntityResult = projectCoverController.uploadResource(projectId);

        assertEquals(HttpStatus.OK, responseEntityResult.getStatusCode());
        assertArrayEquals(bytes, responseEntityResult.getBody());
    }

    @Test
    void testDeleteResourceSuccess() {
        ResourceDto resourceDto = new ResourceDto(key);
        when(projectCoverService.delete(projectId)).thenReturn(resourceDto);

        ResourceDto resourceDtoResult = projectCoverController.deleteResource(projectId);
        assertEquals(resourceDto, resourceDtoResult);
        verify(projectCoverService, times(1)).delete(anyLong());
    }
}