package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParentProjectUpdateTest {
    @InjectMocks
    private ParentProjectUpdate parentProjectUpdate;

    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto = new ProjectDto();
    private Project project = new Project();

    @Test
    void testIsApplicableFalse() {
        assertFalse(parentProjectUpdate.isApplicable(projectDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectDto.setParentProjectId(3L);
        assertTrue(parentProjectUpdate.isApplicable(projectDto));
    }

    @Test
    void testApply() {
        projectDto.setParentProjectId(3L);

        Mockito.when(projectRepository.getProjectById(3L)).thenReturn(new Project());

        parentProjectUpdate.apply(project, projectDto);
        assertNotNull(project.getParentProject());
    }
}