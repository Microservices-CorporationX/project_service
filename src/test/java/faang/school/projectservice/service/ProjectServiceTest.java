package faang.school.projectservice.service;

import faang.school.projectservice.exceptions.FileSizeExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.utils.image.ImageUtils;
import faang.school.projectservice.validator.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageUtils imageUtils;

    private long projectId;

    @BeforeEach
    void setUp() {
        projectId = 1L;
    }

    @Test
    public void testAddNewCover() {
        // arrange
        MockMultipartFile file = getMultiPartFile();
        InputStream inputStream = new ByteArrayInputStream("some text".getBytes());
        String coverImageId = "imageId";
        Project project = new Project();

        when(imageUtils.getResizedBufferedImage(eq(file), anyInt(), anyInt()))
                .thenReturn(mock(BufferedImage.class));
        when(imageUtils.getBufferedImageInputStream(eq(file), any(BufferedImage.class)))
                .thenReturn(inputStream);

        when(s3Service.uploadFile(
                eq(file),
                eq(inputStream),
                anyString()))
                .thenReturn(coverImageId);

        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        // act
        projectService.addCover(projectId, file);

        // assert
        assertEquals(coverImageId, project.getCoverImageId());
    }

    @Test
    public void testAddNewCoverDeletesOldCoverImage() {
        // arrange
        MockMultipartFile file = getMultiPartFile();
        String oldCoverImageId = "oldCoverImageId";
        Project project = new Project();
        project.setCoverImageId(oldCoverImageId);

        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        // act
        projectService.addCover(projectId, file);

        // assert
        verify(s3Service).deleteFile(oldCoverImageId);
    }

    @Test
    public void testAddNewCoverFailsValidation() {
        // arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[5000]
        );

        doThrow(FileSizeExceededException.class)
                .when(fileValidator).validateFileIsImage(file);

        // act and assert
        assertThrows(FileSizeExceededException.class,
                () -> projectService.addCover(projectId, file));
    }

    @Test
    public void testFindProjectById() {
        // Arrange
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        // Act
        Project result = projectService.findProjectById(projectId);

        // Assert
        assertEquals(project, result);
    }

    private MockMultipartFile getMultiPartFile() {
        return new MockMultipartFile(
                "file",
                "file.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[5000]
        );
    }
}