package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    ProjectRepository projectRepositoryMock;
    @InjectMocks
    ProjectServiceImpl projectService;
    Project sampleProject;

    @BeforeEach
    void setUp() {
        sampleProject = Project.builder()
                .id(1L)
                .name("Project 1")
                .description("Desc 1")
                .status(ProjectStatus.CREATED)
                .build();
    }

    //@Test
    //@DisplayName("Test get Project")
/*    void getProject() {
        Optional<Project> expectedResult = Optional.ofNullable(sampleProject);
        Mockito.when(projectRepositoryMock.findById(1L)).thenReturn(expectedResult);
        Optional<Project> optionalProject = projectService.getProject(1L);
        Project project = optionalProject.orElse(new Project());

        Assertions.assertEquals(project, sampleProject);
    }
*/

}