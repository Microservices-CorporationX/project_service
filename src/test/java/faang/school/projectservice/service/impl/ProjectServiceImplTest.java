package faang.school.projectservice.service.impl;

import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    ProjectRepository projectRepositoryMock;
    @Spy
    ProjectMapperImpl projectMapper;
    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    @DisplayName("Test get Project")
    void testGetProjectsByIds() {
        List<Long> projectIds = List.of(1L,2L,3L);
        projectService.getProjectsByIds(projectIds);
        Mockito.verify(projectRepositoryMock, Mockito.times(1)).findAllById(projectIds);
    }
}