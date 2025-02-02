
package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.*;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectService projectService;

    private Project crearteProject;
    private Project updateProject;
    private ProjectCreateRequestDto createRequestDto;
    private ProjectUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        crearteProject = new Project();
        crearteProject.setOwnerId(100L);
        crearteProject.setName("Test Project");
        crearteProject.setStatus(ProjectStatus.CREATED);

        updateProject = new Project();
        updateProject.setId(1L);
        updateProject.setVisibility(ProjectVisibility.PUBLIC);
        updateProject.setStatus(ProjectStatus.CREATED);

        createRequestDto = new ProjectCreateRequestDto();
        createRequestDto.setOwnerId(100L);
        createRequestDto.setName("Test Project");

        updateRequestDto = new ProjectUpdateRequestDto();
        updateRequestDto.setId(1L);
    }

    @Test
    void createProject_ShouldSaveProjectWhenValidRequest() {
        when(projectRepository.existsByOwnerIdAndName(100L, "Test Project")).thenReturn(false);

        when(projectRepository.save(crearteProject)).thenReturn(crearteProject);

        ProjectCreateResponseDto result = projectService.createProject(createRequestDto);


        assertEquals(projectMapper.toCreateResponseDto(crearteProject), result);
        verify(projectRepository).save(crearteProject);
    }

    @Test
    void createProject_ShouldThrowExceptionWhenProjectWithSameNameExists() {
        when(projectRepository.existsByOwnerIdAndName(100L, "Test Project")).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> projectService.createProject(createRequestDto));
        assertEquals("User 100 already has a project with name Test Project",
                dataValidationException.getMessage());
    }

    @Test
    void updateProject_ShouldUpdateProjectWhenProjectExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(updateProject));
        when(projectRepository.save(updateProject)).thenReturn(updateProject);

        ProjectUpdateResponseDto result = projectService.updateProject(updateRequestDto);

        assertEquals(projectMapper.toUpdateResponseDto(updateProject), result);
        verify(projectRepository).save(updateProject);
    }

    @Test
    void updateProject_ShouldThrowExceptionWhenProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> projectService.updateProject(updateRequestDto));
    }

    @Test
    void getAllVisibleProjects_ShouldReturnFilteredProjects() {
        Long userId = 100L;
        Project privateProject = new Project();
        privateProject.setVisibility(ProjectVisibility.PRIVATE);
        privateProject.setOwnerId(userId);
        List<Project> projects = List.of(crearteProject, privateProject);
        ProjectFilterDto filterDto = new ProjectFilterDto();

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponseDto> result = projectService.getAllVisibleProjects(userId, filterDto);

        assertEquals(2, result.size());
        verify(projectRepository).findAll();
    }

    @Test
    void getAllVisibleProjects_ShouldNotReturnPrivateProjects() {
        Project privateProject = new Project();
        privateProject.setVisibility(ProjectVisibility.PRIVATE);
        privateProject.setOwnerId(101L);
        List<Project> projects = List.of(crearteProject, privateProject);
        Long userId = 100L;
        ProjectFilterDto filterDto = new ProjectFilterDto();

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponseDto> result = projectService.getAllVisibleProjects(userId, filterDto);

        assertEquals(1, result.size());
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectById_ShouldReturnProjectWhenExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(crearteProject));

        Project result = projectService.getProjectById(1L);

        assertEquals(crearteProject, result);
    }

    @Test
    void getProjectById_ShouldThrowExceptionWhenNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> projectService.getProjectById(1L));
        assertEquals("Project not found", noSuchElementException.getMessage());
    }

    @Test
    void deleteProjectById_ShouldCallRepositoryDelete() {
        projectService.deleteProjectById(1L);

        verify(projectRepository).deleteById(1L);
    }
}