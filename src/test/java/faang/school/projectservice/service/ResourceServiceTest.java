package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.handler.ResourceHandler;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private ResourceHandler resourceHandler;
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private StorageService storageService;
    @Mock
    private ProjectService projectService;
    @Mock
    private MultipartFile mockFile;
    @Mock
    private Project mockProject;
    @Mock
    private BufferedImage bufferedImage;

    @InjectMocks
    private ResourceService resourceService;

    private long userId;
    private long projectId;
    private String coverImageKey;
    private int maxCoverWidth;
    private int maxCoverHeight;

    @BeforeEach
    void setUp() {
        userId = 1L;
        projectId = 1L;
        coverImageKey = "project_covers/1/cover.jpg";
        maxCoverWidth = ResourceValidator.MAX_COVER_WIDTH_PX;
        maxCoverHeight = ResourceValidator.MAX_COVER_HEIGHT_PX;

        doNothing().when(projectValidator).validateProjectExistsById(projectId);
        doNothing().when(projectValidator).validateUserIsProjectOwner(userId, projectId);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);
    }

    @Test
    void testUploadProjectCover_Success() {
        doNothing().when(resourceValidator).validateResourceNotEmpty(mockFile);
        doNothing().when(resourceValidator).validateProjectCoverSize(mockFile);
        when(resourceHandler.getImageFromMultipartFile(mockFile)).thenReturn(bufferedImage);
        when(resourceValidator.isCorrectProjectCoverScale(bufferedImage)).thenReturn(true);
        when(resourceHandler.convertImageToMultipartFile(mockFile, bufferedImage)).thenReturn(mockFile);
        when(projectValidator.hasProjectCoverImage(mockProject)).thenReturn(false);

        String result = resourceService.uploadProjectCover(mockFile, userId, projectId);

        verify(resourceHandler, times(1)).getImageFromMultipartFile(mockFile);
        verify(resourceHandler, never()).resizeImage(bufferedImage, maxCoverWidth, maxCoverHeight);
        verify(storageService, times(1)).uploadResource(any(MultipartFile.class), anyString());
        verify(projectService, times(1)).saveProject(mockProject);
        assertThat(result).isNotNull();
    }

    @Test
    void testDeleteProjectCover_Success() {
        when(projectValidator.hasProjectCoverImage(mockProject)).thenReturn(true);
        when(mockProject.getCoverImageId()).thenReturn(coverImageKey);

        resourceService.deleteProjectCover(userId, projectId);

        verify(mockProject, times(1)).getCoverImageId();
        verify(mockProject, times(1)).setCoverImageId(null);
        verify(projectService, times(1)).saveProject(mockProject);
        verify(storageService, times(1)).deleteResource(coverImageKey);
    }

    @Test
    void testDeleteProjectCover_NoCoverImage() {
        when(projectValidator.hasProjectCoverImage(mockProject)).thenReturn(false);

        assertThatThrownBy(() -> resourceService.deleteProjectCover(userId, projectId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Project #%d has no cover image to delete.", projectId));
    }

    @Test
    void testDownloadProjectCover_Success() {
        byte[] coverBytes = new byte[]{1, 2, 3};
        when(mockProject.getCoverImageId()).thenReturn(coverImageKey);
        when(storageService.downloadResource(coverImageKey)).thenReturn(coverBytes);

        byte[] result = resourceService.downloadProjectCover(userId, projectId);

        verify(storageService, times(1)).downloadResource(coverImageKey);
        verify(projectValidator).validateUserIsProjectOwner(userId, projectId);
        assertThat(result).isEqualTo(coverBytes);
    }
}