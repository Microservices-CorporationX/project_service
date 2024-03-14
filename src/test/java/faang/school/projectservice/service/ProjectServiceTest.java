package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Spy
    private ProjectMapper projectMapper;

    long requestUserId;
    ProjectDto projectDto, projectDto2, projectDto3, projectDto4;
    ProjectStatus projectStatus;
    List<ProjectDto> projectDtoList;

    @BeforeEach
    public void init() {
        requestUserId = 1L;
        projectStatus = ProjectStatus.CREATED;

        projectDto = new ProjectDto();
        projectDto.setId( 1L );
        projectDto.setName( "Project 1" );
        projectDto.setDescription( "Description of project 1" );
        projectDto.setVisibility( ProjectVisibility.PUBLIC );
        projectDto.setStatus( ProjectStatus.CREATED );
        projectDto.setOwnerId( 1L );


        projectDto2 = new ProjectDto();
        projectDto2.setId( 2L );
        projectDto2.setName( "Project 2" );
        projectDto2.setDescription( "Description of project 2" );
        projectDto2.setVisibility( ProjectVisibility.PRIVATE );
        projectDto2.setStatus( ProjectStatus.IN_PROGRESS );
        projectDto2.setOwnerId( 2L );

        projectDto3 = new ProjectDto();
        projectDto3.setId( 3L );
        projectDto3.setName( "Project 3" );
        projectDto3.setDescription( "Description of project 3" );
        projectDto3.setVisibility( ProjectVisibility.PUBLIC );
        projectDto3.setStatus( ProjectStatus.COMPLETED );
        projectDto3.setOwnerId( 1L );

        projectDto4 = new ProjectDto();
        projectDto4.setId( 4L );
        projectDto4.setName( "Project 4" );
        projectDto4.setDescription( "Description of project 4" );
        projectDto4.setVisibility( ProjectVisibility.PRIVATE );
        projectDto4.setStatus( ProjectStatus.CREATED );
        projectDto4.setOwnerId( 3L );

        projectDtoList = new ArrayList<>();
        projectDtoList.add( projectDto );
        projectDtoList.add( projectDto2 );
        projectDtoList.add( projectDto3 );
        projectDtoList.add( projectDto4);
    }

    @Test
    public void testProjectExistsByOwnerIdAndName() {

        when(projectJpaRepository.existsByOwnerIdAndName(  requestUserId, projectDto.getName() )).thenReturn( true );
        assertThrows( IllegalStateException.class, () -> projectService.save( projectDto, requestUserId ) );
    }

    @Test
    public void testSaveNewProjectSuccess() {
        Project expectedProject = Project.builder()
                .id( 1L )
                .name( "Project 1" )
                .description( "Description of project 1" )
                .visibility( ProjectVisibility.PUBLIC )
                .status( ProjectStatus.CREATED  )
                .ownerId( requestUserId )
                .build();


//        when(projectMapper.toEntity( projectDto )).thenReturn( project );
//        when(projectJpaRepository.save(project)).thenReturn(project);
//
//        projectService.save(projectDto, requestUserId);
//
//        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
//        verify(projectJpaRepository, times(1)).save(argumentCaptor.capture());
//        Project capturedUser = argumentCaptor.getValue();
//
//        assertEquals(project.getName(), capturedUser.getName());
//        assertEquals(project.getId(), capturedUser.getId());

        when(projectJpaRepository.save(any(Project.class))).thenReturn(expectedProject);
        ProjectDto actual = projectService.save(projectDto, requestUserId);

        assertEquals( expectedProject, actual );
    }

    @Test
    public void testUpdateProjectIdDoesNotExist() {

        when(projectJpaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                projectService.updateProject(1L, anyString(), projectStatus, requestUserId));

        verify(projectJpaRepository).findById(1L);

        verifyNoInteractions(projectMapper);

        verify(projectJpaRepository, times( 0 )).save( any() );
    }

    @Test
    public void testFindProjectsByName_ProjectsFoundAndAccessible() {
        String projectName = "Project1";
        long userId = 123;

        Project project1 = new Project();
        project1.setId(1L);
        project1.setName(projectName);
        project1.setVisibility(ProjectVisibility.PUBLIC);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName(projectName);
        project2.setVisibility(ProjectVisibility.PRIVATE);
        project2.setTeams( Arrays.asList(new Team())); // Simulating membership

        List<Project> projects = Arrays.asList(project1, project2);

        when(projectJpaRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDtoList(projects)).thenReturn(projectMapper.toDtoList( projects )); // Mocking DTO conversion

        // Act
        List<ProjectDto> result = projectService.findProjectsByName(projectDto.getName(), requestUserId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify that projects are filtered by name and visibility
        assertEquals(2, projects.size()); // Ensure both projects are retrieved initially
        assertEquals(1, result.size()); // Ensure only one project is returned in the result
        assertEquals(project2.getId(), result.get(0).getId()); // Ensure the correct project is returned

    }





}
