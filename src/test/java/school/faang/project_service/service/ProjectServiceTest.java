package school.faang.project_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.filters.FilterProjects;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private SubProjectValidator subProjectValidator;

    @Mock
    private ProjectValidator projectValidator;

    @InjectMocks
    private ProjectService projectService;

    private Project parentProject;
    private Project subProject;
    private Project mappedProject;

    private Project subProject1;
    private Project subProject2;
    private FilterSubProjectDto filters;

    @BeforeEach
    public void setUp() {
        parentProject = new Project();
        parentProject.setId(1L);

        subProject1 = new Project();
        subProject1.setId(2L);

        subProject2 = new Project();
        subProject2.setId(3L);

        parentProject.setChildren(List.of(subProject1, subProject2));
        filters = new FilterSubProjectDto("name", ProjectStatus.CREATED, ProjectVisibility.PUBLIC);
    }

    @Test
    public void testCreateSubProject_Success() {
        Long parentId = 1L;
        when(projectRepository.findById(parentId)).thenReturn(Optional.of(parentProject));
        doNothing().when(projectValidator).doesProjectExist(Optional.of(parentProject));
        doNothing().when(subProjectValidator).canBeParentProject(parentProject);
        when(projectMapper.toNewEntity(subProject, parentProject)).thenReturn(mappedProject);
        when(projectRepository.save(mappedProject)).thenReturn(mappedProject);

        Project result = projectService.createSubProject(parentId, subProject);

        assertNotNull(result);
        assertEquals(mappedProject.getId(), result.getId());
        verify(projectRepository).findById(parentId);
        verify(projectValidator).doesProjectExist(Optional.of(parentProject));
        verify(subProjectValidator).canBeParentProject(parentProject);
        verify(projectMapper).toNewEntity(subProject, parentProject);
        verify(projectRepository).save(mappedProject);
    }

    @Test
    public void testCreateSubProject_ParentNotFound() {
        Long parentId = 1L;
        when(projectRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> projectService.createSubProject(parentId, subProject));

        verify(projectRepository).findById(parentId);
        verify(projectValidator).doesProjectExist(Optional.empty());

    }

    @Test
    public void testUpdateSubProject_Success() {
        Long projectId = 2L;
        ProjectStatus status = ProjectStatus.COMPLETED;
        ProjectVisibility visibility = ProjectVisibility.PUBLIC;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(subProject1));
        doNothing().when(projectValidator).doesProjectExist(Optional.of(subProject1));
        doNothing().when(subProjectValidator).childCompleted(subProject1.getChildren());
        when(projectRepository.save(subProject1)).thenReturn(subProject1);

        Project result = projectService.updateSubProject(projectId, status, visibility);

        assertNotNull(result);
        assertEquals(status, result.getStatus());
        assertEquals(visibility, result.getVisibility());

    }


    @Test
    public void testGetSubProjects_Success() {
        Long parentId = 1L;
        Integer limit = 2;
        FilterProjects filter = mock(FilterProjects.class);

        when(filter.isApplicable(filters)).thenReturn(true);
        when(filter.apply(any(), eq(filters)))
                .thenAnswer(invocation -> ((Stream<Project>) invocation.getArgument(0)).limit(2));
        when(projectRepository.findById(parentId)).thenReturn(Optional.of(parentProject));
        doNothing().when(projectValidator).doesProjectExist(Optional.of(parentProject));
        doNothing().when(subProjectValidator).shouldBePublic(parentProject);
        //when(projectFilters.stream()).thenReturn(Stream.of(filter));

        List<Project> result = projectService.getSubProjects(parentId, filters, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(projectRepository).findById(parentId);
        verify(projectValidator).doesProjectExist(Optional.of(parentProject));
        verify(subProjectValidator).shouldBePublic(parentProject);
    }

    @Test
    public void testGetSubProjects_ParentNotFound() {
        Long parentId = 1L;
        when(projectRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.getSubProjects(parentId, filters, 2));

        verify(projectRepository).findById(parentId);
        verify(projectValidator).doesProjectExist(Optional.empty());
        //verifyNoMoreInteractions(subProjectValidator, projectFilters);
    }
}

