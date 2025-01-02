package faang.school.projectservice.service;

import faang.school.projectservice.exception.FileWriteReadS3Exception;
import jakarta.validation.ValidationException;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utilities.ImageConvert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectCoverServiceTest {
    @Mock
    private MultipartFile file;
    @Mock
    private ImageConvert imageConvert;
    @Mock
    private S3Service s3Service;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectCoverService projectCoverService;

    private String bucketName;
    private String folder;
    private Long id;
    private String key;
    private Project project;

    @BeforeEach
    public void setUp(){
        ReflectionTestUtils.setField(projectCoverService, "bucketName", "storage_test");
        ReflectionTestUtils.setField(projectCoverService, "folder", "Project-cover-test");
        ReflectionTestUtils.setField(projectCoverService, "targetImageSize", 100);

        bucketName = "storage_test";
        folder = "Project-cover-test";

        id = 1L;

        key = "1234-1234-1234-12345678";
        project = new Project();
        project.setId(id);
    }

    @Test
    void addSuccessTest() throws IOException {
        int targetImageSize = 100;

        String oldKey = folder + "/B234-1234-1234-12345678";
        project.setCoverImageId(oldKey);

        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(s3Service.getKeyName()).thenReturn(key);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(file.getContentType()).thenReturn("jpg");
        when(imageConvert.resizeImageJpg(file.getInputStream(),targetImageSize)).thenReturn(new ByteArrayInputStream(new byte[0]));

        doNothing().when(s3Service).toS3File(
                eq(bucketName),
                argThat(keyNew -> keyNew.equals(folder + "/" + key)),
                argThat(contentTypeNew -> contentTypeNew.equals(file.getContentType())),
                argThat(Objects::nonNull));

        when(projectRepository.save(project)).thenReturn(project);
        doNothing().when(s3Service).deleteFile(bucketName, oldKey);

        ResourceDto result = projectCoverService.add(id, file);

        assertNotNull(result);
        assertEquals(folder + "/" + key, result.idImage());

        verify(projectRepository).save(project);
        verify(s3Service).deleteFile(bucketName, oldKey);
    }

    @Test
    void addFailTest() throws IOException {
        int targetImageSize = 100;
        String message = "Error add!";

        String oldKey = folder + "/B234-1234-1234-12345678";
        project.setCoverImageId(oldKey);

        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(s3Service.getKeyName()).thenReturn(key);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(file.getContentType()).thenReturn("jpg");
        when(imageConvert.resizeImageJpg(file.getInputStream(),targetImageSize)).thenReturn(new ByteArrayInputStream(new byte[0]));

        doThrow( new FileWriteReadS3Exception(message)).when(s3Service).toS3File(
                anyString(),
                anyString(),
                anyString(),
                any());

        FileWriteReadS3Exception exception  = assertThrows( FileWriteReadS3Exception.class, () -> projectCoverService.add(id, file));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void uploadSuccessTest() {
        String newKey = folder + "/" + key;

        project.setCoverImageId(newKey);

        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(s3Service.fromS3File(bucketName, project.getCoverImageId())).thenReturn(new ByteArrayInputStream(new byte[0]));

        InputStream inputStream = projectCoverService.upload(id);

        assertNotNull(inputStream);
    }

    @Test
    void deleteSuccessTest() {
        String newKey = folder + "/" + key;

        project.setCoverImageId(newKey);

        when(projectRepository.getProjectById(id)).thenReturn(project);
        doNothing().when(s3Service).deleteFile(bucketName, project.getCoverImageId());

        ResourceDto result = projectCoverService.delete(id);

        assertNotNull(result);
    }

    @Test
    void deleteFalseTest() {
        project.setCoverImageId(null);

        when(projectRepository.getProjectById(id)).thenReturn(project);

        ValidationException exception = assertThrows(ValidationException.class, () -> projectCoverService.delete(1L));
        assertEquals("The KeyId is Null for the project id 1", exception.getMessage());
        verify(s3Service, never()).deleteFile(anyString(), anyString());
    }
}