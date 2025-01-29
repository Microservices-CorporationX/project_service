
package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.*;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
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

import java.util.ArrayList;
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
    void getProjectDtoById_ShouldReturnProjectWhenExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(crearteProject));

        ProjectResponseDto result = projectService.getProjectDtoById(1L);

        assertEquals(projectMapper.toResponseDto(crearteProject), result);
    }

    @Test
    void getProjectDtoById_ShouldThrowExceptionWhenNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> projectService.getProjectDtoById(1L));
        assertEquals("Project not found", noSuchElementException.getMessage());
    }

    @Test
    void deleteProjectById_ShouldCallRepositoryDelete() {
        projectService.deleteProjectById(1L);

        verify(projectRepository).deleteById(1L);
    }
}
//package faang.school.projectservice.service;
//
//import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
//import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
//import faang.school.projectservice.dto.project.ProjectFilterDto;
//import faang.school.projectservice.dto.project.ProjectResponseDto;
//import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
//import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
//import faang.school.projectservice.filter.project.ProjectFilter;
//import faang.school.projectservice.mapper.ProjectMapper;
//import faang.school.projectservice.model.Project;
//import faang.school.projectservice.model.ProjectStatus;
//import faang.school.projectservice.repository.ProjectRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mapstruct.factory.Mappers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ProjectServiceTest {
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Spy
//    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
//    @Spy
//    private List<ProjectFilter> projectFilters = new ArrayList<>();
//    @InjectMocks
//    private ProjectService projectService;
//
//    @Test
//    void testCreateProject_Success() {
//        Project projectToRepository = new Project();
//        projectToRepository.setStatus(ProjectStatus.CREATED);
//
//        Project expectedProject = new Project();
//        expectedProject.setId(1L);
//        expectedProject.setStatus(ProjectStatus.CREATED);
//        when(projectRepository.save(projectToRepository)).thenReturn(expectedProject);
//        ProjectCreateRequestDto input = new ProjectCreateRequestDto();
//
//        ProjectCreateResponseDto result = projectService.createProject(input);
//
//        verify(projectRepository, times(1)).save(projectToRepository);
//        assertEquals(projectMapper.toCreateResponseDto(expectedProject), result);
//    }
//
//    @Test
//    void testUpdateProject_Success() {
//        Project existingProject = new Project();
//        existingProject.setId(1L);
//        existingProject.setName("testName");
//        existingProject.setDescription("testDescription");
//        existingProject.setStatus(ProjectStatus.CREATED);
//        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
//
//        ProjectUpdateRequestDto input = new ProjectUpdateRequestDto();
//        input.setId(1L);
//        input.setDescription("testDescription1");
//        input.setStatus(ProjectStatus.CREATED);
//
//        Project projectToRepository = new Project();
//        projectToRepository.setId(1L);
//        projectToRepository.setName("testName1");
//        projectToRepository.setDescription("testDescription1");
//        projectToRepository.setStatus(ProjectStatus.CREATED);
//
//        Project expectedProject = new Project();
//        expectedProject.setId(1L);
//        expectedProject.setName("testName1");
//        expectedProject.setDescription("testDescription1");
//        expectedProject.setStatus(ProjectStatus.CREATED);
//        when(projectRepository.save(projectToRepository)).thenReturn(expectedProject);
//
//        ProjectUpdateResponseDto result = projectService.updateProject(input);
//
//        verify(projectRepository, times(1)).save(projectToRepository);
//        assertEquals(projectMapper.toUpdateResponseDto(expectedProject), result);
//    }
//
//    @Test
//    void testUpdateProject_NotFound() {
//        ProjectUpdateRequestDto requestDto = new ProjectUpdateRequestDto();
//        requestDto.setId(1L);
//
//        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> projectService.updateProject(requestDto));
//        verify(projectRepository, never()).save(any());
//    }
//
//    @Test
//    void testGetAllProjects_Success() {
//        ProjectFilterDto filterDto = new ProjectFilterDto();
//        List<Project> projects = Arrays.asList(new Project(), new Project());
//        when(projectRepository.findAll()).thenReturn(projects);
//
//        List<ProjectResponseDto> result = projectService.getAllProjects(filterDto);
//
//        assertEquals(2, result.size());
//        verify(projectMapper, times(2)).toResponseDto(any());
//    }
//
//    @Test
//    void testGetAllProjects_NoFilters() {
//        List<Project> projects = Arrays.asList(new Project(), new Project());
//        when(projectRepository.findAll()).thenReturn(projects);
//
//        ProjectFilterDto filterDto = new ProjectFilterDto();
//
//        List<ProjectResponseDto> result = projectService.getAllProjects(filterDto);
//
//        assertEquals(2, result.size());
//        verify(projectMapper, times(2)).toResponseDto(any());
//    }
//
//    @Test
//    void testGetAllProjects_NoProjectsFound() {
//        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
//        ProjectFilterDto filterDto = new ProjectFilterDto();
//
//        List<ProjectResponseDto> result = projectService.getAllProjects(filterDto);
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void testGetProjectDtoById_Success() {
//        Project project = new Project();
//        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
//
//        ProjectResponseDto result = projectService.getProjectDtoById(1L);
//
//        assertEquals(projectMapper.toResponseDto(project), result);
//    }
//
//    @Test
//    void testGetProjectDtoById_NotFound() {
//        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> projectService.getProjectDtoById(1L));
//    }
//
//    @Test
//    void testDeleteProjectById() {
//        projectService.deleteProjectById(1L);
//
//        verify(projectRepository, times(1)).deleteById(1L);
//    }
//}