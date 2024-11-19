package faang.school.projectservice.subproject;

import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectFilterDto.SubprojectFilterDto;
import faang.school.projectservice.exception.Subproject.SubprojectBadRequestException;
import faang.school.projectservice.mapper.subprojectMapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subprojectService.SubProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {

    @InjectMocks
    private SubProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private SubProjectService subProjectService;

    private Project parentProject;
    private CreateSubProjectDto createSubProjectDto;
    private Project subProject;
    private ProjectDto expectedProjectDto;
    private SubprojectFilterDto subprojectFilterDto;

    @BeforeEach
    void setUp() {
        parentProject = Project.builder()
                .id(1L)
                .name("Parent Project")
                .description("Description of Parent Project")
                .children(Arrays.asList(subProject))
                .build();

        createSubProjectDto = new CreateSubProjectDto();
        createSubProjectDto.setName("Subproject 1");
        createSubProjectDto.setDescription("Description of Subproject");
        createSubProjectDto.setStatus(ProjectStatus.CREATED);

        expectedProjectDto = new ProjectDto();
        expectedProjectDto.setId(2L);
        expectedProjectDto.setName("Subproject 1");
        expectedProjectDto.setDescription("Description of Subproject");
        expectedProjectDto.setOwnerId(1L);

        subProject = Project.builder()
                .id(2L)
                .name("Subproject 1")
                .description("Description of Subproject")
                .parentProject(parentProject)
                .build();
    }

    @Test
    void createSubProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);

        when(projectRepository.save(any(Project.class))).thenReturn(subProject);

        when(projectMapper.toEntity(createSubProjectDto)).thenReturn(subProject);
        when(projectMapper.toDto(subProject)).thenReturn(expectedProjectDto);

        ProjectDto createdSubProject = projectService.createSubProject(1L, createSubProjectDto);

        verify(projectRepository).getProjectById(1L);
        verify(projectRepository).save(any(Project.class));
        verify(projectMapper).toEntity(createSubProjectDto);
        verify(projectMapper).toDto(subProject);

        assertNotNull(createdSubProject);
        assertEquals(expectedProjectDto.getId(), createdSubProject.getId());
        assertEquals(expectedProjectDto.getName(), createdSubProject.getName());
        assertEquals(expectedProjectDto.getDescription(), createdSubProject.getDescription());
        assertEquals(expectedProjectDto.getOwnerId(), createdSubProject.getOwnerId());
    }

    @Test
    void testUpdateSubProject_EntityNotFoundException() {
        when(projectRepository.getProjectById(1L)).thenReturn(null);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Subproject with ID 1 not found", exception.getMessage());
    }

    @Test
    void testUpdateSubProject_ParentProjectCancelled() {
        parentProject.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);

        Exception exception = assertThrows(SubprojectBadRequestException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Subproject ID mismatch", exception.getMessage());
    }
    @Test
    void testUpdateSubProject_ParentProjectCompletedWithOpenSubProjects() {
        parentProject.setStatus(ProjectStatus.COMPLETED);
        Project openSubProject = new Project();
        openSubProject.setStatus(ProjectStatus.CREATED);
        parentProject.setChildren(Arrays.asList(openSubProject));

        when(projectRepository.getProjectById(1L)).thenReturn(subProject);

        Exception exception = assertThrows(SubprojectBadRequestException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Subproject ID mismatch", exception.getMessage());
    }

    @Test
    void testUpdateSubProject_Success() {
        parentProject.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(new ProjectDto());

        createSubProjectDto.setId(1L);

        Project updatedSubProject = new Project();
        updatedSubProject.setName(createSubProjectDto.getName());
        updatedSubProject.setDescription(createSubProjectDto.getDescription());
        when(projectRepository.save(any(Project.class))).thenReturn(updatedSubProject);

        ProjectDto result = projectService.updateSubProject(1L, createSubProjectDto);

        assertNotNull(result);
        assertEquals(createSubProjectDto.getName(), updatedSubProject.getName());
        assertEquals(createSubProjectDto.getDescription(), updatedSubProject.getDescription());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateSubProject_ParentProjectPrivate() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(new ProjectDto());

        createSubProjectDto.setId(1L);

        Project updatedSubProject = new Project();
        updatedSubProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.save(any(Project.class))).thenReturn(updatedSubProject);

        projectService.updateSubProject(1L, createSubProjectDto);

        assertEquals(ProjectVisibility.PRIVATE, updatedSubProject.getVisibility());
    }

    @Test
    void testUpdateSubProject_SaveError() {
        when(projectRepository.getProjectById(1L)).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Database error"));

        createSubProjectDto.setId(1L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.updateSubProject(1L, createSubProjectDto);
        });

        assertEquals("Failed to update subproject", exception.getMessage());
    }

    @Test
    void testGetSubProjectsByFilter() {
        SubprojectFilterDto.builder().name("Subproject 1").status(ProjectStatus.CREATED).build();
        when(subProjectService.getSubProject(1L, subprojectFilterDto)).thenReturn(List.of(expectedProjectDto));
        List<ProjectDto> subProjects = subProjectService.getSubProject(1L, subprojectFilterDto);
        assertEquals("Subproject 1", subProjects.get(0).getName());
        assertNotNull(subProjects);
    }

    @Test
    void testGetSubProjectsByFilter_negative() {
        SubprojectFilterDto.builder().name("Subproject 1").status(ProjectStatus.CREATED).build();
        when(subProjectService.getSubProject(2L, subprojectFilterDto)).thenReturn(List.of());
        List<ProjectDto> subProjects = subProjectService.getSubProject(2L, subprojectFilterDto);
        assertEquals(0, subProjects.size());
    }
}