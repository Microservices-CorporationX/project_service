package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.*;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.retriever.project.create_request.ProjectCreateRetriever;
import faang.school.projectservice.retriever.project.request.ProjectRetriever;
import faang.school.projectservice.retriever.project.update_request.ProjectUpdateRetriever;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    @Spy
    private List<ProjectFilter> projectFilters = List.of();
    @Spy
    private List<ProjectCreateRetriever> projectCreateRetrievers = List.of();
    @Spy
    private List<ProjectUpdateRetriever> projectUpdateRetrievers = List.of();
    @Spy
    private List<ProjectRetriever> projectRetrievers = List.of();
    @InjectMocks
    private ProjectService projectService;

    @Test
    void testCreateProject_Success() {
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0);
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            ProjectCreateRequestDto input = new ProjectCreateRequestDto();
            Project expectedProject = new Project();
            expectedProject.setId(1L);
            expectedProject.setCreatedAt(fixedTime);
            expectedProject.setStatus(ProjectStatus.CREATED);
            when(projectRepository.save(any(Project.class))).thenReturn(expectedProject);

            ProjectResponseDto result = projectService.createProject(input);

            verify(projectRepository, times(1)).save(any(Project.class));
            assertEquals(projectMapper.toResponseDto(expectedProject), result);
        }
    }

    @Test
    void testUpdateProject_Success() {
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0);
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            Project existingProject = new Project();
            existingProject.setId(1L);
            existingProject.setName("testName");
            existingProject.setDescription("testDescription");
            existingProject.setStatus(ProjectStatus.CREATED);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

            ProjectUpdateRequestDto input = new ProjectUpdateRequestDto();
            input.setId(1L);
            input.setName("testName1");
            input.setDescription("testDescription1");

            Project expectedProject = new Project();
            expectedProject.setId(1L);
            expectedProject.setUpdatedAt(fixedTime);
            expectedProject.setStatus(ProjectStatus.CREATED);
            when(projectRepository.save(any(Project.class))).thenReturn(expectedProject);

            ProjectResponseDto result = projectService.updateProject(input);

            verify(projectRepository, times(1)).save(any(Project.class));
            assertEquals(projectMapper.toResponseDto(expectedProject), result);
        }
    }

    @Test
    void testUpdateProject_NotFound() {
        ProjectUpdateRequestDto requestDto = new ProjectUpdateRequestDto();
        requestDto.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> projectService.updateProject(requestDto));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testGetAllProjects_Success() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        List<Project> projects = Arrays.asList(new Project(), new Project());
        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponseDto> result = projectService.getAllProjects(filterDto);

        assertEquals(2, result.size());
        verify(projectMapper, times(2)).toResponseDto(any());
    }

    @Test
    void testGetProjectDtoById_Success() {
        Project project = new Project();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponseDto result = projectService.getProjectDtoById(1L);

        assertEquals(projectMapper.toResponseDto(project), result);
    }

    @Test
    void testGetProjectDtoById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> projectService.getProjectDtoById(1L));
    }

    @Test
    void testDeleteProjectById() {
        projectService.deleteProjectById(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetProjectsByIds() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(projectRepository.findAllById(ids)).thenReturn(projects);

        List<Project> result = projectService.getProjectsByIds(ids);

        assertEquals(projects, result);
    }
}