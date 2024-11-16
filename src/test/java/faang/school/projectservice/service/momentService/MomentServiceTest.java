package faang.school.projectservice.service.momentService;

import faang.school.projectservice.dto.momentDto.MomentDto;
import faang.school.projectservice.dto.momentDto.MomentFilterDto;
import faang.school.projectservice.exception.momentException.DataValidationException;
import faang.school.projectservice.exception.momentException.MomentNotFoundException;
import faang.school.projectservice.mapper.momentMapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.momentService.filter.MomentFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentFilter mockMomentFilter;

    List<MomentFilter> momentFilters;
    @Test
    public void testCreateProjectCancelled() {
        Project firstProject = createProject(1L, "Project 1", ProjectStatus.CANCELLED);
        Project secondProject = createProject(2L, "Project 2", ProjectStatus.IN_PROGRESS);
        MomentDto momentDto = createMomentDto(1L, "Moment");
        momentDto.setProjectIds(List.of(firstProject.getId(), secondProject.getId()));
        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(List.of(firstProject, secondProject));

        assertThrows(DataValidationException.class, () -> momentService.create(momentDto));
    }

    @Test
    public void testCreateMomentSuccessfully() {
        Project firstProject = createProject(1L, "Project 1", ProjectStatus.IN_PROGRESS);
        Project secondProject = createProject(2L, "Project 2", ProjectStatus.IN_PROGRESS);
        MomentDto momentDto = createMomentDto(1L, "Moment");
        momentDto.setProjectIds(List.of(firstProject.getId(), secondProject.getId()));
        Moment moment = new Moment();
        moment.setId(momentDto.getId());
        moment.setProjects(List.of(firstProject, secondProject));
        moment.setName(momentDto.getName());

        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(List.of(firstProject, secondProject));
        when(momentMapper.toDto(any(Moment.class))).thenReturn(momentDto);
        when(momentMapper.toEntity(any(MomentDto.class))).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);

        MomentDto result = momentService.create(momentDto);

        verify(projectRepository, times(2)).findAllByIds(momentDto.getProjectIds());
        verify(momentRepository, times(1)).save(moment);
        verifyNoMoreInteractions(momentRepository, projectRepository);

        assertNotNull(result);
        assertEquals(result.getId(), moment.getId());
        assertEquals(result.getName(), moment.getName());
        assertEquals(result.getProjectIds().get(0), moment.getProjects().get(0).getId());
    }

    @Test
    public void testUpdateMomentUserOrProjectIdExists() {
        Project firstProject = createProject(1L, "Project1", ProjectStatus.IN_PROGRESS);
        Project secondProject = createProject(2L, "Project2", ProjectStatus.IN_PROGRESS);
        List<Long> userIds = new ArrayList<>(List.of(1L, 2L, 3L, 4L));
        Long userIdToAdd = 1L;
        Long projectIdToAdd = 1L;
        Moment moment = createMoment(1L, "Moment");
        moment.setUserIds(userIds);
        moment.setProjects(List.of(firstProject, secondProject));
        MomentDto momentDto = createMomentDto(1L, "Moment");
        momentDto.setProjectIds(List.of(firstProject.getId(), secondProject.getId()));
        momentDto.setUserIds(userIds);
        when(momentRepository.findById(moment.getId())).thenReturn(Optional.of(moment));

        assertThrows(DataValidationException.class, () -> momentService.update(
                moment.getId(),
                userIdToAdd,
                projectIdToAdd));
    }

    @Test
    public void testUpdateMomentSuccessfully() {
        Project firstProject = createProject(1L, "Project1", ProjectStatus.IN_PROGRESS);
        Project secondProject = createProject(2L, "Project2", ProjectStatus.IN_PROGRESS);
        Project thirdProject = createProject(5L, "Project5", ProjectStatus.IN_PROGRESS);
        List<Long> userIds = new ArrayList<>(List.of(1L, 2L, 3L, 4L));
        Long userIdToAdd = 5L;
        Long projectIdToAdd = 5L;
        Moment moment = createMoment(1L, "Moment");
        moment.setUserIds(userIds);
        moment.setProjects(List.of(firstProject, secondProject));
        MomentDto momentDto = createMomentDto(1L, "Moment");
        momentDto.setProjectIds(List.of(firstProject.getId(), secondProject.getId()));
        momentDto.setUserIds(userIds);
        when(momentRepository.findById(moment.getId())).thenReturn(Optional.of(moment));
        when(projectRepository.getProjectById(projectIdToAdd)).thenReturn(thirdProject);
        when(momentMapper.toDto(any())).thenReturn(momentDto);

        MomentDto result = momentService.update(moment.getId(), userIdToAdd, projectIdToAdd);
        verify(momentRepository, times(1)).findById(moment.getId());
        verify(projectRepository, times(1)).getProjectById(projectIdToAdd);
        verify(momentRepository, times(1)).save(moment);
        assertNotNull(result);
        assertEquals(result.getId(), moment.getId());
    }

    @Test
    public void testGetMomentsWhenAllNull() {
        MomentFilterDto filter = createMomentFilterDto(1L, null, new ArrayList<>());
        List<Moment> moments = getMoments();

        when(momentRepository.findAll()).thenReturn(moments);

        List<MomentDto> result = momentService.getMomentsByFilter(filter);

        verify(momentRepository, times(1)).findAll();
        verifyNoMoreInteractions(momentRepository);
        assertNotNull(result);
        assertNotEquals(filter.getProjectIds().size(), result.size());
    }

    @Test
    public void testGetMomentsWhenSomeValuesNull() {
        MomentFilterDto filter = createMomentFilterDto(1L, LocalDateTime.now(), new ArrayList<>());
        List<Moment> moments = getMoments();
        when(momentRepository.findAll()).thenReturn(moments);
        List<MomentDto> result = momentService.getMomentsByFilter(filter);

        verify(momentRepository, times(1)).findAll();
        verifyNoMoreInteractions(momentRepository);
        assertNotNull(result);
        assertNotEquals(filter.getProjectIds().size(), result.size());
    }

    @Test
    public void testGetMomentsWithFiltersSuccessfully() {
        List<Moment> moments = getMoments();
        Project firstProject = moments.get(0).getProjects().get(0);
        Project secondProject = moments.get(1).getProjects().get(1);
        MomentFilterDto filter = createMomentFilterDto(1L, LocalDateTime.now(), List.of(firstProject.getId(), secondProject.getId()));
        MomentDto firstMomentDto = createMomentDto(1L, "Moment1");
        MomentDto secondMomentDto = createMomentDto(2L, "Moment2");
        firstMomentDto.setProjectIds(filter.getProjectIds());
        secondMomentDto.setProjectIds(filter.getProjectIds());
        firstMomentDto.setDate(filter.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        secondMomentDto.setDate(filter.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Moment firstMoment = createMoment(1L, "Moment1");
        Moment secondMoment = createMoment(2L, "Moment2");
        firstMoment.setProjects(List.of(firstProject, secondProject));
        secondMoment.setProjects(List.of(firstProject, secondProject));
        firstMoment.setDate(filter.getDate());
        secondMoment.setDate(filter.getDate());
        when(momentRepository.findAll()).thenReturn(moments);
        when(momentMapper.toDto(any(Moment.class))).thenReturn(firstMomentDto);

        List<MomentDto> result = momentService.getMomentsByFilter(filter);

        verify(momentRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(filter.getProjectIds().size(), result.size());
    }

    @Test
    public void testGetMomentsSuccessfully() {
        List<Moment> moments = getMoments();
        when(momentRepository.findAll()).thenReturn(moments);
        List<MomentDto> result = momentService.getMoments();
        assertNotNull(result);
        assertEquals(moments.size(), result.size());
    }

    @Test
    public void testGetMomentByIdNotFound() {
        Moment moment = createMoment(3L, "Moment1");
        when(momentRepository.findById(moment.getId())).thenReturn(Optional.empty());
        assertThrows(MomentNotFoundException.class, () -> momentService.getMomentById(moment.getId()));
    }

    @Test
    public void testGetMomentByIdSuccess() {
        Moment moment = createMoment(1L, "Moment1");
        MomentDto momentDto = createMomentDto(1L, "Moment1");
        when(momentRepository.findById(moment.getId())).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.getMomentById(moment.getId());
        verify(momentRepository, times(1)).findById(moment.getId());
        verifyNoMoreInteractions(momentRepository);
        assertNotNull(result);
        assertEquals(result.getId(), moment.getId());
        assertEquals(result.getName(), moment.getName());
    }

    private MomentDto createMomentDto(long id, String name) {
        MomentDto momentDto = new MomentDto();
        momentDto.setId(id);
        momentDto.setName(name);
        return momentDto;
    }

    private Project createProject(long id, String name, ProjectStatus status) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setStatus(status);
        return project;
    }

    private Moment createMoment(long id, String name) {
        Moment moment = new Moment();
        moment.setId(id);
        moment.setName(name);
        return moment;
    }

    private List<Moment> getMoments() {
        Project firstProject = createProject(1L, "Project 1", ProjectStatus.IN_PROGRESS);
        Project secondProject = createProject(2L, "Project 2", ProjectStatus.IN_PROGRESS);
        Moment firstMoment = createMoment(1L, "Moment1");
        firstMoment.setProjects(List.of(firstProject, secondProject));

        Moment secondMoment = createMoment(2L, "Moment2");
        secondMoment.setProjects(List.of(firstProject, secondProject));

        return new ArrayList<>(List.of(firstMoment, secondMoment));
    }

    private MomentFilterDto createMomentFilterDto(long id, LocalDateTime date, List<Long> projectIds) {
        return new MomentFilterDto(id, date, projectIds);
    }

    @BeforeEach
    void init() {
        momentFilters = List.of(mockMomentFilter);
        momentService = new MomentService(momentRepository, momentMapper, projectRepository, momentFilters);
    }
}
