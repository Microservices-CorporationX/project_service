package school.faang.project_service.service;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.fillters.project.ProjectFilter;
import faang.school.projectservice.fillters.project.impl.ProjectNameFilter;
import faang.school.projectservice.fillters.project.impl.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectEntityMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectManagementServiceTest {
    private static final long OWNER_ID = 2L;
    private static final long PROJECT_ID = 1L;

    private ProjectRepository projectRepository;
    private ProjectEntityMapperImpl projectMapper;
    private ProjectManagementService projectManagementService;
    private List<ProjectFilter> projectFilters;
    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    private ProjectCreateDto createDto;
    private ProjectUpdateDto updateDto;
    private ProjectFilterDto filterDto;

    @BeforeEach
    public void init(){
        createDto = new ProjectCreateDto();
        createDto.setId(1L);
        createDto.setName("Test Project");
        createDto.setDescription("Test Description");
        createDto.setOwnerId(OWNER_ID);

        updateDto = new ProjectUpdateDto();
        updateDto.setId(1L);
        updateDto.setDescription("Updated Description");
        updateDto.setStatus(ProjectStatus.IN_PROGRESS);

        filterDto = new ProjectFilterDto();
        filterDto.setNamePattern("Test");
        filterDto.setStatusPattern(ProjectStatus.CREATED);

        projectRepository = mock(ProjectRepository.class);
        projectMapper = spy(ProjectEntityMapperImpl.class);
        projectFilters = List.of(mock(ProjectNameFilter.class), mock(ProjectStatusFilter.class));
        projectManagementService = new ProjectManagementService(projectRepository, projectMapper, projectFilters);
    }

    @Test
    void createProjectShouldThrowExceptionIfProjectExists() {
        mockExistByOwnerIdAndName(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> projectManagementService.createProject(createDto)
        );

        assertEquals("Проект с таким названием уже существует для этого владельца", exception.getMessage());
    }


    @Test
    void createProjectShouldSaveProjectSuccessfully() {
        mockExistByOwnerIdAndName(false);

        projectManagementService.createProject(createDto);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project project = projectCaptor.getValue();
        assertEquals(createDto.getName(), project.getName());
        assertEquals(ProjectStatus.CREATED, project.getStatus());

    }

    @Test
    void updateProjectShouldThrowExceptionIfNotFound() {
        when(projectRepository.findById(updateDto.getId())).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectManagementService.updateProject(updateDto, OWNER_ID)
        );

        assertEquals("Не существует проекта с ID: " + updateDto.getId(), exception.getMessage());
    }

    @Test
    void updateProjectShouldUpdateSuccessfully() {
        Project existingProject = new Project();
        existingProject.setId(updateDto.getId());
        existingProject.setDescription("Description");
        existingProject.setStatus(ProjectStatus.CREATED);

        mockFindProjectById(existingProject);

        projectManagementService.updateProject(updateDto, OWNER_ID);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project updatedProject = projectCaptor.getValue();

        assertEquals(updateDto.getDescription(), updatedProject.getDescription());
        assertEquals(updateDto.getStatus(), updatedProject.getStatus());
    }

    @Test
    void getAllProjectsWithFiltersShouldApplyFiltersAndReturnProjects() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Test Project 1");
        project1.setStatus(ProjectStatus.CREATED);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Other Project");
        project2.setStatus(ProjectStatus.COMPLETED);

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        mockProjectFiltersReturnStream(Stream.of(project1));

        List<ProjectInfoDto> filteredProjects = projectManagementService.getAllProjectsWithFilters(filterDto, OWNER_ID);

        assertEquals(1, filteredProjects.size());
        assertEquals("Test Project 1", filteredProjects.get(0).getName());
    }

    @Test
    void getAllProjectsShouldReturnVisibleProjects() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setOwnerId(OWNER_ID);
        project1.setVisibility(ProjectVisibility.PUBLIC);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setOwnerId(OWNER_ID);
        project2.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectInfoDto> projects = projectManagementService.getAllProjects(OWNER_ID);

        assertEquals(2, projects.size());
        assertEquals(1L, projects.get(0).getId());
        assertEquals(2L, projects.get(1).getId());
    }



    @Test
    void getProjectByIdShouldThrowExceptionIfNotVisible() {

        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setOwnerId(5L);
        project.setVisibility(ProjectVisibility.PRIVATE);

        mockFindProjectById(project);

        NoAccessException exception = assertThrows(
                NoAccessException.class,
                () -> projectManagementService.getProjectById(PROJECT_ID, OWNER_ID)
        );

        assertEquals("У вас нет доступа к проекту с id: " + PROJECT_ID, exception.getMessage());
    }

    @Test
    void getProjectByIdShouldReturnProjectIfVisible() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setOwnerId(OWNER_ID);
        project.setVisibility(ProjectVisibility.PRIVATE);

        mockFindProjectById(project);

        ProjectInfoDto result = projectManagementService.getProjectById(PROJECT_ID, OWNER_ID);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
    }

    private void mockExistByOwnerIdAndName(boolean t) {
        when(projectRepository.existsByOwnerIdAndName(createDto.getOwnerId(), createDto.getName())).thenReturn(t);
    }

    private void mockFindProjectById(Project project) {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
    }

    private void mockProjectFiltersReturnStream(Stream<Project> stream) {
        projectFilters.forEach(projectFilter -> {
            when(projectFilter.isApplicable(any())).thenReturn(true);
            when(projectFilter.apply(any(), any())).thenReturn(stream);
        });
    }

}
