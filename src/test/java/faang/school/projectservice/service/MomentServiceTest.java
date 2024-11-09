package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.momentFilter.MomentDateFilter;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectService projectService;

    @Spy
    private MomentMapperImpl momentMapper;

    @Mock
    private MomentDateFilter momentDateFilter;

    @Mock
    private ProjectValidator projectValidator;


    private MomentService momentService;

    private Moment moment1;
    private Moment moment2;

    private MomentDto momentDto1;
    private MomentDto momentDto2;
    private List<Filter<Moment, MomentFilterDto>> filters;

    @BeforeEach
    void setUp() {
        filters = List.of(momentDateFilter);
        momentService = new MomentService(momentRepository, projectService, momentMapper, projectValidator, filters);
        moment1 = Moment.builder()
                .id(1L)
                .name("Moment 1")
                .projects(new ArrayList<>())
                .createdAt(LocalDateTime.of(2023, Month.MARCH, 10, 12, 0))
                .build();
        moment2 = Moment.builder()
                .id(2L)
                .name("Moment 2")
                .createdAt(LocalDateTime.of(2023, Month.APRIL, 15, 10, 0))
                .build();
        momentDto1 = MomentDto.builder()
                .id(1L)
                .name("Moment 1")
                .build();
        momentDto2 = MomentDto.builder()
                .id(2L)
                .name("Moment 2").build();

    }

    private void mockMomentMapper() {
        lenient().when(momentMapper.toDto(moment1)).thenReturn(momentDto1);
        lenient().when(momentMapper.toDto(moment2)).thenReturn(momentDto2);
    }

    @Test
    public void saveMomentWhenAllProjectsIsOpen() {
        momentDto1.setProjectsIds(List.of(1L, 2L));
        when(projectValidator.isOpenProject(1L)).thenReturn(true);
        when(projectValidator.isOpenProject(2L)).thenReturn(true);

        assertDoesNotThrow(() -> momentService.saveMoment(momentDto1));
        verify(momentRepository, times(1)).save(any(Moment.class));
        verify(momentMapper, times(1)).toEntity(momentDto1);
    }

    @Test
    public void saveMomentWhenSomeProjectsIsOpen() {
        momentDto1.setProjectsIds(List.of(1L, 2L));
        when(projectValidator.isOpenProject(1L)).thenReturn(true);
        when(projectValidator.isOpenProject(2L)).thenReturn(false);

        assertDoesNotThrow(() -> momentService.saveMoment(momentDto1));
        verify(momentRepository, times(1)).save(any(Moment.class));
        verify(momentMapper, times(1)).toEntity(momentDto1);
    }

    @Test
    public void saveMomentWhenProjectsIsNotOpen() {
        momentDto1.setProjectsIds(List.of(1L, 2L));
        when(projectValidator.isOpenProject(1L)).thenReturn(false);
        when(projectValidator.isOpenProject(2L)).thenReturn(false);

        MomentDto savedMomentDto = momentService.saveMoment(momentDto1);

        assertTrue(moment1.getProjects().isEmpty());
        verify(momentMapper, times(1)).toEntity(momentDto1);
    }

    @Test
    public void updateMomentWhenMomentExists() {
        Long momentId = 1L;
        momentDto1.setId(momentId);
        momentDto1.setProjectsIds(List.of(10L, 20L));
        moment1.setId(momentId);
        moment1.setProjects(new ArrayList<>());
        Project project1 = new Project();
        project1.setId(10L);
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment1));
        when(projectService.findAllById(momentDto1.getProjectsIds())).thenReturn(List.of(project1));

        momentService.updateMoment(momentDto1);

        assertEquals(1, moment1.getProjects().size());
        assertEquals(10L, moment1.getProjects().get(0).getId());
        verify(momentRepository, times(1)).findById(momentId);
        verify(momentRepository, times(1)).save(moment1);

    }

    @Test
    public void updateMomentWhenMomentNotFound() {
        Long momentId = 1L;
        MomentDto momentDto = new MomentDto();
        momentDto.setId(momentId);
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> momentService.updateMoment(momentDto));
        verify(momentRepository, never()).save(any(Moment.class));
    }

    @Test
    public void testGetMomentsWithFilter() {
        mockMomentMapper();
        MomentFilterDto filterDto = MomentFilterDto.builder()
                .month(Month.MARCH)
                .projectIds(Collections.emptyList())
                .build();
        when(momentRepository.findAll()).thenReturn(List.of(moment1, moment2));
        when(momentDateFilter.isApplicable(filterDto)).thenReturn(true);
        when(momentDateFilter.apply(any(), eq(filterDto))).thenAnswer(invocation -> Stream.of(moment1));

        List<MomentDto> result = momentService.getMoments(filterDto);

        assertEquals(1, result.size());
        assertEquals(momentDto1, result.get(0));
        verify(momentDateFilter, times(1)).isApplicable(any(MomentFilterDto.class));
        verify(momentDateFilter, times(1)).apply(any(), eq(filterDto));

    }

    @Test
    void testGetMomentsNotFoundFilters() {
        mockMomentMapper();
        MomentFilterDto filterDto = MomentFilterDto.builder().month(Month.APRIL).projectIds(List.of(9L, 8L)).build();
        when(momentRepository.findAll()).thenReturn(List.of(moment1, moment2));
        when(momentDateFilter.isApplicable(filterDto)).thenReturn(false);

        List<MomentDto> result = momentService.getMoments(filterDto);

        assertEquals(0, result.size());
        verify(momentDateFilter, times(1)).isApplicable(filterDto);
        verify(momentDateFilter, never()).apply(any(Stream.class), eq(filterDto));
    }

    @Test
    public void testGetAllMomentsReturnsListOfMomentDtos() {
        mockMomentMapper();
        when(momentRepository.findAll()).thenReturn(List.of(moment1, moment2));

        List<MomentDto> result = momentService.getAllMoments();

        assertEquals(2, result.size());
        assertEquals(momentDto1, result.get(0));
        assertEquals(momentDto2, result.get(1));
        verify(momentMapper, times(4)).toDto(any());
        verify(momentRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllMomentsReturnsEmptyListWhenNoMoments() {
        when(momentRepository.findAll()).thenReturn(Collections.emptyList());

        List<MomentDto> result = momentService.getAllMoments();

        assertTrue(result.isEmpty());
        verify(momentMapper, times(0)).toDto(any());
        verify(momentRepository, times(1)).findAll();
    }

    @Test
    public void testGetMoment() {
        Long id = 1L;
        when(momentRepository.getById(id)).thenReturn(moment1);
        when(momentMapper.toDto(moment1)).thenReturn(momentDto1);

        MomentDto result = momentService.getMoment(id);
        assertEquals(id, result.getId());
        verify(momentRepository, times(1)).getById(id);
    }

    @Test
    public void testGetMomentNotFound() {
        Long momentId = 1L;
        when(momentRepository.getById(momentId)).thenThrow(new EntityNotFoundException("Moment not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> momentService.getMoment(momentId), "Expected getMoment() to throw, but it didn't");

        assertEquals("Moment not found", exception.getMessage());
        verify(momentMapper, never()).toDto(any());
    }
}