package faang.school.projectservice.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @Mock
    private ProjectServiceImpl projectServiceMock;
    @InjectMocks
    private ResourceValidator resourceValidator;
    private final List<Long> resourceIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 49; i++) {
            resourceIds.add((long) i);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test user is in project")
    void validateUserInProject() {
        Long userId = 1L;
        Long userNotInProjectId = 11L;
        Long projectId = 222L;
        Mockito.when(projectServiceMock.isUserInProject(userId, projectId)).thenReturn(true);
        resourceValidator.validateUserInProject(userId, projectId);

        Mockito.when(projectServiceMock.isUserInProject(userNotInProjectId, projectId)).thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserInProject(userNotInProjectId, projectId));
    }

    @Test
    @DisplayName("Test user can download resource")
    void validateUserCanDownloadResource() {
        Long userInProjectId = 1L;
        Long userNotInProjectId = 2L;
        Long publicProjectId = 222L;
        Long privateProjectId = 223L;
        Mockito.when(projectServiceMock.isProjectPublic(publicProjectId)).thenReturn(true);
        Mockito.when(projectServiceMock.isUserInProject(userInProjectId, publicProjectId)).thenReturn(true);
        resourceValidator.validateUserCanDownloadFromProject(userInProjectId, publicProjectId);

        Mockito.when(projectServiceMock.isProjectPublic(privateProjectId)).thenReturn(false);
        Mockito.when(projectServiceMock.isUserInProject(userInProjectId, privateProjectId)).thenReturn(true);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userInProjectId, privateProjectId));

        Mockito.when(projectServiceMock.isProjectPublic(publicProjectId)).thenReturn(true);
        Mockito.when(projectServiceMock.isUserInProject(userNotInProjectId, publicProjectId)).thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userNotInProjectId, publicProjectId));

        Mockito.when(projectServiceMock.isProjectPublic(privateProjectId)).thenReturn(false);
        Mockito.when(projectServiceMock.isUserInProject(userNotInProjectId, privateProjectId)).thenReturn(false);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateUserCanDownloadFromProject(userNotInProjectId, privateProjectId));
    }

    @Test
    @DisplayName("Test overload resource to project")
    void validateResourcesOversize() {
        Long projectId = 222L;
        Mockito.when(projectServiceMock.getProjectResourceIds(projectId)).thenReturn(resourceIds);
        resourceValidator.validateResourcesOversize(projectId);
        resourceIds.add(1050L);
        resourceValidator.validateResourcesOversize(projectId);
        resourceIds.add(10005L);
        Assert.assertThrows(RuntimeException.class,
                () -> resourceValidator.validateResourcesOversize(projectId));
    }
}