package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.filter.moment.MomentFilter;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Spy
    private MomentMapper momentMapper;

    @Mock
    private ProjectService projectService;

    @Test
    void createMomentNameNullTest() {
        MomentDto momentDto = new MomentDto();
        Moment moment = new Moment();
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(momentDto));
    }

    @Test
    void createMomentProjectStatusDoesNotMatchTest() {
        MomentDto momentDto = new MomentDto();
        Moment moment = new Moment();
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        List<Project> projects = List.of(project);
        moment.setProjects(projects);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(momentDto));

    }

    @Test
    void createMomentTest() {
        Moment moment = new Moment();
        MomentDto momentDto = new MomentDto();
        moment.setName("Test");
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        moment.setProjects(List.of(project));

        when(momentMapper.toEntity(momentDto)).thenReturn(moment);

        momentService.createMoment(momentDto);

        verify(momentMapper, times(1)).toEntity(momentDto);
        verify(momentMapper, times(1)).toDto(moment);
        verify(momentRepository, times(1)).save(moment);
    }

    @Test
    void updateMomentEntityNotFoundTest() {
        Long momentId = 1L;
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());
        MomentDto momentDto = new MomentDto();
        assertThrows(EntityNotFoundException.class, () -> momentService.updateMoment(momentDto, 1L));
    }

    @Test
    void updateMomentTest() {
        Long momentId = 1L;
        MomentDto updatedMomentDto = new MomentDto();
        updatedMomentDto.setName("Test 1");
        updatedMomentDto.setProjectIds(List.of(1L, 2L));

        Moment momentToUpdate = new Moment();
        momentToUpdate.setName("Test 2");

        Moment updatedMoment = new Moment();
        updatedMoment.setName("Test 3");

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        updatedMoment.setProjects(List.of(project));

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(momentToUpdate));
        when(momentMapper.toEntity(updatedMomentDto)).thenReturn(updatedMoment);
        doNothing().when(momentMapper).updateEntity(momentToUpdate, updatedMomentDto);
        when(projectService.findAllById(updatedMomentDto.getProjectIds())).thenReturn(List.of(new Project(), new Project()));

        momentService.updateMoment(updatedMomentDto, momentId);

        verify(momentMapper, times(1)).updateEntity(momentToUpdate, updatedMomentDto);
        verify(momentRepository, times(1)).save(momentToUpdate);
    }

    @Test
    void getMomentsByFilter() {
        List<Moment> moments = Arrays.asList(new Moment(), new Moment());
        when(momentRepository.findAll()).thenReturn(moments);

        MomentFilter filterMock = Mockito.mock(MomentFilter.class);
        List<MomentFilter> filters = List.of(filterMock);

        momentService = new MomentService(momentRepository, momentMapper, projectService, filters);

        MomentFilterDto momentFilterDto = new MomentFilterDto();
        when(filters.get(0).isApplicable(eq(momentFilterDto))).thenReturn(true);
        when(filters.get(0).apply(any(), eq(momentFilterDto))).thenReturn(moments.stream());

        momentService.getMomentsByFilter(momentFilterDto);
        verify(momentMapper, times(moments.size())).toDto(any(Moment.class));
    }

    @Test
    void getAllMomentsTest() {
        List<Moment> moments = List.of(new Moment());
        when(momentRepository.findAll()).thenReturn(moments);
        momentService.getAllMoments();
        verify(momentRepository, times(1)).findAll();
        verify(momentMapper, times(1)).toDto(any(Moment.class));
    }

    @Test
    void getMomentByIdNotFoundTest() {
        Long momentId = 1L;
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> momentService.getMomentById(1L));
    }

    @Test
    void getMomentByIdTest() {
        Long momentId = 1L;
        Moment moment = new Moment();
        MomentDto momentDto = new MomentDto();
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        momentService.getMomentById(momentId);

        verify(momentRepository, times(1)).findById(momentId);
        verify(momentMapper, times(1)).toDto(moment);
    }


}
