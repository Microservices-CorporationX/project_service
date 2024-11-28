package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.filters.MomentFilter;
import faang.school.projectservice.dto.moment.filters.MomentStartDateFromFilter;
import faang.school.projectservice.dto.moment.filters.PartnerProjectFilter;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;

    @Spy
    private MomentMapperImpl momentMapper;
    @InjectMocks
    MomentService momentService;

    @Mock
    MomentFilter momentFilterMock;

    @Mock
    List<MomentFilter> momentFilterListMock;

    @Mock
    MomentFilterDto momentFilterDto;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;
    @Captor
    private ArgumentCaptor<MomentFilterDto> momentFilterCaptor;
    @Captor
    private ArgumentCaptor<MomentDto> momentDtoCaptor;
    @Captor
    private ArgumentCaptor<Moment> momentCaptor;

    MomentDto momentDto = new MomentDto();
    Project firstProject;
    Project secondProject;
    List<MomentDto> momentDtoList;

    Moment moment = new Moment();
    List<Moment> momentList;

    long id = 1;


    @BeforeEach
    public void setupMomentDto() {
        momentDto.setName("Notification feature");
        momentDto.setCreatedAt(LocalDateTime.of(2023, 7, 15, 10, 30));
        firstProject = new Project();
        secondProject = new Project();
        firstProject.setStatus(ProjectStatus.IN_PROGRESS);
        secondProject.setStatus(ProjectStatus.IN_PROGRESS);
        List<Project> partnerProjectList = new ArrayList<>();
        partnerProjectList.add(firstProject);
        partnerProjectList.add(secondProject);
        momentDto.setProjects(partnerProjectList);
    }

    @BeforeEach
    public void setupMomentDtoList() {
        momentDtoList = new ArrayList<>();
        MomentDto firstMomentDto = new MomentDto();
        MomentDto secondMomentDto = new MomentDto();
        firstMomentDto.setName("Redis feature");
        secondMomentDto.setName("Kabana feature");
        momentDtoList.add(firstMomentDto);
        momentDtoList.add(secondMomentDto);
    }

    @BeforeEach
    public void setupMoment() {
        momentDto.setName("Notification feature");
        momentDto.setCreatedAt(LocalDateTime.of(2023, 7, 15, 10, 30));
        firstProject = new Project();
        secondProject = new Project();
        firstProject.setStatus(ProjectStatus.IN_PROGRESS);
        secondProject.setStatus(ProjectStatus.IN_PROGRESS);
        List<Project> partnerProjectList = new ArrayList<>();
        partnerProjectList.add(firstProject);
        partnerProjectList.add(secondProject);
        momentDto.setProjects(partnerProjectList);
    }

    @BeforeEach
    public void setupMomentList() {
        momentList = new ArrayList<>();
        Moment firstMoment = new Moment();
        Moment secondMoment = new Moment();
        firstMoment.setName("Redis feature");
        secondMoment.setName("Kibana feature");
        momentList.add(firstMoment);
        momentList.add(secondMoment);

        momentFilterMock = Mockito.mock(MomentFilter.class);
        PartnerProjectFilter filterPartnerProjectMock = Mockito.mock(PartnerProjectFilter.class);
        MomentStartDateFromFilter filterStartDateFromMock = Mockito.mock(MomentStartDateFromFilter.class);
        momentFilterListMock = List.of(filterPartnerProjectMock, filterStartDateFromMock);
    }

    @Test
    public void testCreate() {
        Moment mappedMoment = momentMapper.toEntity(momentDto);
        when(momentRepository.save(mappedMoment)).thenReturn(mappedMoment);

        MomentDto savedMomentDto = momentService.create(momentDto);
        verify(momentRepository, times(1)).save(momentCaptor.capture());
        Moment capturedMoment = momentCaptor.getValue();
        assertEquals(mappedMoment, capturedMoment);
        assertEquals(momentDto, savedMomentDto);
    }

    @Test
    public void testUpdate() {
        Moment mappedMoment = momentMapper.toEntity(momentDto);
        when(momentRepository.save(mappedMoment)).thenReturn(mappedMoment);

        MomentDto updatedMomentDto = momentService.update(momentDto);
        verify(momentRepository, times(1)).save(momentCaptor.capture());
        Moment capturedMoment = momentCaptor.getValue();
        assertEquals(mappedMoment, capturedMoment);
        assertEquals(momentDto, updatedMomentDto);
    }

    @Test
    public void testGetAllMoments() {
        List<Moment> mappedMomentList = momentMapper.toEntityList(momentDtoList);
        when(momentRepository.findAll()).thenReturn(mappedMomentList);

        List<MomentDto> receivedMomentDtoList = momentService.getAllMoments();
        verify(momentRepository, times(1)).findAll();
        assertEquals(momentDtoList, receivedMomentDtoList);
    }

    @Test
    public void testGetMomentByIdWithPresentId() {

        when(momentRepository.findById(id)).thenReturn(Optional.of(moment));

        MomentDto receivedMomentDto = momentService.getMomentById(id);
        verify(momentRepository, times(1)).findById(longArgumentCaptor.capture());
        long capturedId = longArgumentCaptor.getValue();
        assertEquals(id, capturedId);
        assertEquals(momentMapper.toDto(moment), receivedMomentDto);
    }

    @Test
    public void testGetMomentByIdWithAbsentId() {
        Optional<Moment> optionalMoment = Optional.empty();
        when(momentRepository.findById(id)).thenReturn(optionalMoment);

        assertThrows(ValidationException.class, () -> momentService.getMomentById(id));
    }

    @Test
    public void testGetMomentsByFilter() {
        when(momentRepository.findAll()).thenReturn(momentList);

        List<MomentDto> receivedMomentDtoList = momentService.getMomentsByFilter(momentFilterDto);
        verify(momentRepository, times(1)).findAll();
        assertEquals(momentList, momentMapper.toEntityList(receivedMomentDtoList));
    }

}
