package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectValidator projectValidator;

    @InjectMocks
    private ProjectService projectService;

    private Project parentProject;
    private Project publicSubProject1;
    private Project publicSubProject2;
    private Project publicSubProject3;
    private Project privateSubProject;


    @BeforeEach
    public void beforeEach() {
        parentProject = new Project();
        parentProject.setId(1L);

        publicSubProject1 = new Project();
        publicSubProject1.setId(2L);
        publicSubProject1.setVisibility(ProjectVisibility.PUBLIC);
        publicSubProject1.setName("A Project");
        publicSubProject1.setStatus(ProjectStatus.COMPLETED);

        publicSubProject2 = new Project();
        publicSubProject2.setId(3L);
        publicSubProject2.setVisibility(ProjectVisibility.PUBLIC);
        publicSubProject2.setName("B Project");
        publicSubProject2.setStatus(ProjectStatus.IN_PROGRESS);

        publicSubProject3 = new Project();
        publicSubProject3.setId(4L);
        publicSubProject3.setVisibility(ProjectVisibility.PUBLIC);
        publicSubProject3.setName("C Project");
        publicSubProject3.setStatus(ProjectStatus.CREATED);

        privateSubProject = new Project();
        privateSubProject.setId(5L);
        privateSubProject.setVisibility(ProjectVisibility.PRIVATE);
        privateSubProject.setName("Private Project");

        parentProject.setChildren(Arrays.asList(publicSubProject1, publicSubProject2, privateSubProject, publicSubProject3));
    }

    @Test
    public void testCreate() {
        CreateSubProjectDto createDto = new CreateSubProjectDto();
        Project subProject = projectMapper.toEntity(createDto);

        projectService.create(createDto);
        Mockito.verify(projectRepository, Mockito.times(1)).save(subProject);
    }

    @Test
    public void testUpdate() {
        Project project = Project.builder().id(1L).build();
        UpdateSubProjectDto updateDto = new UpdateSubProjectDto();
        updateDto.setId(1L);

        Mockito.when(projectRepository.findById(updateDto.getId())).thenReturn(Optional.of(project));
        projectMapper.updateEntityFromDto(updateDto, project);

        projectService.update(updateDto);
        Mockito.verify(projectRepository, Mockito.times(1)).save(project);
    }

    @Test
    public void testGetSubProjects() {
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));

        List<ProjectDto> subProjects = projectService.getSubProjects(1L);

        assertEquals(3, subProjects.size());

        assertEquals("C Project", subProjects.get(0).getName());
        assertEquals("B Project", subProjects.get(1).getName());
        assertEquals("A Project", subProjects.get(2).getName());
    }
}
