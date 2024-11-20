package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.filter.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.moment.MomentMonthFilter;
import faang.school.projectservice.filter.moment.MomentProjectFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.moment.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentMapper momentMapper;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private ProjectRepository projectRepository;
    private List<Filter<Moment, MomentFilterDto>> momentFilters;

    @BeforeEach
    public void setUp() {
        Filter<Moment, MomentFilterDto> dateFilter = mock(MomentMonthFilter.class);
        Filter<Moment, MomentFilterDto> projectFilter = mock(MomentProjectFilter.class);
        momentFilters = new ArrayList<>(List.of(dateFilter, projectFilter));
        momentService = new MomentService(momentRepository, momentMapper, momentValidator, projectRepository, momentFilters);
    }

    @Test
    void testCreateMoment() {
        MomentDto momentDto = MomentDto.builder().id(1L).projectIds(Collections.singletonList(1L)).build();
        Moment moment = new Moment();
        moment.setId(1L);

        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(Collections.emptyList());
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto createdMoment = momentService.createMoment(momentDto);

        assertNotNull(createdMoment);
        assertEquals(momentDto, createdMoment);
        verify(momentValidator).validateUniqueMoment(momentDto);
        verify(momentValidator).validateActiveMoment(moment);
        verify(momentRepository).save(moment);
    }

    @Test
    void testUpdateMoment() {
        MomentDto momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setProjectIds(Collections.singletonList(1L));

        Moment moment = new Moment();
        moment.setId(1L);

        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(Collections.emptyList());
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto updatedMoment = momentService.updateMoment(momentDto);

        assertNotNull(updatedMoment);
        assertEquals(momentDto, updatedMoment);
        verify(momentMapper, times(1)).updateEntity(moment, momentDto);
        verify(momentValidator).validateMomentExists(momentDto.getId());
        verify(momentRepository).save(moment);
    }

    @Test
    void testGetMomentsByFilter(){
        MomentDto dto1 = MomentDto.builder().id(1L).projectIds(Collections.singletonList(1L)).build();

        Moment moment1 = new Moment();
        Moment moment2 = new Moment();
        moment1.setId(1L);
        moment2.setId(2L);

        MomentFilterDto filters = MomentFilterDto.builder().build();

        when(momentRepository.findAll()).thenReturn(Arrays.asList(moment1, moment2));
        when(momentFilters.get(0).isApplicable(filters)).thenReturn(true);
        when(momentFilters.get(1).isApplicable(filters)).thenReturn(true);
        when(momentFilters.get(0).apply(any(), any())).thenReturn(Stream.of(moment1));
        when(momentFilters.get(1).apply(any(), any())).thenReturn(Stream.of(moment1));

        when(momentMapper.toDto(moment1)).thenReturn(dto1);

        List<MomentDto> result = momentService.getMomentsByFilter(filters);
        assertEquals(List.of(dto1), result);
    }

    @Test
    void testGetAllMoments(){
        MomentDto dto1 = MomentDto.builder().id(1L).projectIds(Collections.singletonList(1L)).build();
        MomentDto dto2 = MomentDto.builder().id(2L).projectIds(Collections.singletonList(3L)).build();

        Moment moment1 = new Moment();
        Moment moment2 = new Moment();
        moment1.setId(1L);
        moment2.setId(2L);

        when(momentMapper.toDto(moment1)).thenReturn(dto1);
        when(momentMapper.toDto(moment2)).thenReturn(dto2);
        when(momentRepository.findAll()).thenReturn(Arrays.asList(moment1, moment2));

        List<MomentDto> result = momentService.getAllMoments();
        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);
    }

    @Test
    void testGetMomentByIdPositive(){
        Long id = 1L;
        MomentDto dto = MomentDto.builder().id(id).projectIds(Collections.singletonList(1L)).build();
        Moment moment = new Moment();
        moment.setId(id);

        when(momentRepository.findById(id)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(dto);

        MomentDto result = momentService.getMomentById(id);
        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void testGetMomentByIdNegative(){
        Long id = 1L;
        when(momentRepository.findById(id)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
            momentService.getMomentById(id)
        );
        assertEquals("Moment with id:1 not found", exception.getMessage());
    }
}