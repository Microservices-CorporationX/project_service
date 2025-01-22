package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.SubProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectUpdateDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
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

    @Mock
    private List<SubProjectFilter> subProjectFilter;

    @InjectMocks
    private ProjectService projectService;

    private Project parentProject;
    private Project publicSubProject1;
    private Project publicSubProject2;

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

        parentProject.setChildren(Arrays.asList(publicSubProject1, publicSubProject2));
    }

    @Test
    public void testCreate() {
        SubProjectCreateDto createDto = new SubProjectCreateDto();
        Project subProject = projectMapper.toEntity(createDto);

        projectService.create(createDto);
        Mockito.verify(projectRepository, Mockito.times(1)).save(subProject);
    }

    @Test
    public void testUpdate() {
        Project project = Project.builder().id(1L).build();
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto();
        updateDto.setId(1L);

        Mockito.when(projectRepository.findById(updateDto.getId())).thenReturn(Optional.of(project));
        projectMapper.updateEntityFromDto(updateDto, project);

        projectService.update(updateDto);
        Mockito.verify(projectRepository, Mockito.times(1)).save(project);
    }

    @Test
    public void testGetSubProjects() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));

        List<ProjectReadDto> subProjects = projectService.getSubProjects(1L, filterDto);

        assertEquals(0, subProjects.size());
    }
}
