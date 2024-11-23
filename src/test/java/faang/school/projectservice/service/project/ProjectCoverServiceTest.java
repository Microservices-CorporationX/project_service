package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.CoverProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.validator.CoverProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectCoverServiceTest {

    @InjectMocks
    private ProjectCoverService projectCoverService;

    @Mock
    private ProjectService projectService;

    @Mock
    private CoverProjectValidator coverProjectValidator;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageResizer imageResizer;

    private Project project;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(1L);
    }

    @Test
    public void testGetCoverProject() {
        long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(project);

        projectCoverService.getCoverProject(projectId);
    }

    @Test
    public void addCoverProject() {
        long projectId = 1L;
        byte[] coverImage = new byte[]{1, 2, 3};
        MultipartFile image = new MockMultipartFile("test", coverImage);
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        String key = "key";
        ReflectionTestUtils.setField(
                projectCoverService,
                "URI",
                "https://test-endpoint.com/test-bucket/"
        );

        when(projectService.getProjectById(projectId)).thenReturn(project);
        doNothing().when(coverProjectValidator).validation(any(), any());
        when(imageResizer.resizeImage(any())).thenReturn(byteArrayInputStream);
        when(s3Service.uploadFile(any(), any())).thenReturn(key);

        CoverProjectDto dto = projectCoverService.addCoverProject(projectId, image);

        verify(projectService).saveProject(project);

        String[] paths = dto.getURI().split("/");
        assertEquals(key, paths[paths.length - 1]);
    }

    @Test
    public void testDeleteCoverProject() {
        long projectId = 1L;

        when(projectService.getProjectById(projectId)).thenReturn(project);
        doNothing().when(coverProjectValidator).validation(any());

        CoverProjectDto dto = projectCoverService.deleteCoverProject(projectId);

        verify(projectService).saveProject(project);
        assertTrue(dto.isDeleted());
        assertNull(project.getCoverImageId());
    }
}
