package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.momentFilter.MomentDateFilter;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
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

    @InjectMocks
    private MomentService momentService;
    private Moment moment1;
    private Moment moment2;

    private MomentDto momentDto1;
    private MomentDto momentDto2;
    private List<Filter<Moment, MomentFilterDto>> filters;

    @BeforeEach
    void setUp() {
        filters = List.of(momentDateFilter);
        momentService = new MomentService(momentRepository, projectService, momentMapper, filters);
        moment1 = Moment.builder().id(1L).name("Moment 1").createdAt(LocalDateTime.of(2023, Month.MARCH, 10, 12, 0)).build();
        moment2 = Moment.builder().id(2L).name("Moment 2").createdAt(LocalDateTime.of(2023, Month.APRIL, 15, 10, 0)).build();
        momentDto1 = MomentDto.builder().id(1L).name("Moment 1").build();
        momentDto2 = MomentDto.builder().id(2L).name("Moment 2").build();
    }

    private void mockMomentMapper() {
        lenient().when(momentMapper.toDto(moment1)).thenReturn(momentDto1);
        lenient().when(momentMapper.toDto(moment2)).thenReturn(momentDto2);
    }

    @Test
    public void saveMomentWhenAllProjectsIsOpen() {
        MomentDto momentDto = new MomentDto();
        momentDto.setProjectsIds(List.of(1L, 2L));
        when(projectService.projectIsOpen(1L)).thenReturn(true);
        when(projectService.projectIsOpen(2L)).thenReturn(true);//

        assertDoesNotThrow(() -> momentService.saveMoment(momentDto));
        verify(momentRepository, times(1)).save(any(Moment.class));
        verify(momentMapper, times(1)).toEntity(momentDto);
    }

    @Test
    public void saveMomentWhenSomeProjectsIsOpen() {
        MomentDto momentDto = new MomentDto();
        momentDto.setProjectsIds(List.of(1L, 2L));
        when(projectService.projectIsOpen(1L)).thenReturn(true);
        when(projectService.projectIsOpen(2L)).thenReturn(false);

        assertDoesNotThrow(() -> momentService.saveMoment(momentDto));
        verify(momentRepository, times(1)).save(any(Moment.class));
        verify(momentMapper, times(1)).toEntity(momentDto);
    }

    @Test
    public void saveMomentWhenMomentExists() {
        Long momentId = 1L;
        MomentDto momentDto = new MomentDto();
        momentDto.setId(momentId);
        Moment moment = Moment.builder().build();
        moment.setId(momentId);
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));

        momentService.updateMoment(momentDto);

        verify(momentRepository, times(1)).findById(momentId);
        verify(momentRepository, times(1)).save(moment);

    }

    @Test
    public void saveMomentWhenMomentNotFound() {
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
        MomentFilterDto filterDto = MomentFilterDto.builder().month(Month.MARCH).projectIds(Collections.emptyList()).build();
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

        assertEquals(2, result.size());
        assertEquals(momentDto1, result.get(0));
        assertEquals(momentDto2, result.get(1));
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
    }

    @Test
    public void testGetAllMomentsReturnsEmptyListWhenNoMoments() {
        when(momentRepository.findAll()).thenReturn(Collections.emptyList());

        List<MomentDto> result = momentService.getAllMoments();

        assertTrue(result.isEmpty());
        verify(momentMapper, times(0)).toDto(any());
    }

    @Test
    public void testGetMoment() {
        Long id = 1L;
        when(momentRepository.getReferenceById(id)).thenReturn(moment1);
        when(momentMapper.toDto(moment1)).thenReturn(momentDto1);

        MomentDto result = momentService.getMoment(id);

        verify(momentRepository, times(1)).getReferenceById(id);
    }

    @Test
    public void testGetMomentNotFound() {
        Long momentId = 1L;
        when(momentRepository.getReferenceById(momentId)).thenThrow(new EntityNotFoundException("Moment not found"));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> momentService.getMoment(momentId),
                "Expected getMoment() to throw, but it didn't");
        assertEquals("Moment not found", exception.getMessage());
        verify(momentMapper, never()).toDto(any());
    }
}