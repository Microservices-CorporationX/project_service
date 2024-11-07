package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> captor;

    private ProjectDto projectDto;
    private Project project;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .build();

        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .build();
    }

    @Test
    void testCreateProjectSuccessful() {
        doNothing().when(projectValidator).validateUniqueProject(projectDto.getName(), projectDto.getOwnerId());
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        project.setStatus(ProjectStatus.CREATED);

        projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(captor.capture());
        Project result = captor.getValue();
        assertEquals(result, project);
    }
}