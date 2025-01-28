package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwnerId(1L);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCanUserAccessProjectReturnsTrueWhenProjectIsPublic() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PUBLIC);
        project.setOwnerId(2L);

        assertTrue(projectValidator.canUserAccessProject(project, 3L));
    }

    @Test
    void testCanUserAccessProjectReturnsTrueWhenUserIsOwner() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PRIVATE);
        project.setOwnerId(1L);

        assertTrue(projectValidator.canUserAccessProject(project, 1L));
    }
}