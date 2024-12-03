package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.filters.MomentFilter;
import faang.school.projectservice.dto.moment.filters.MomentStartDateFromFilter;
import faang.school.projectservice.dto.moment.filters.PartnerProjectFilter;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.mapper.ProjectMapperProjectDtoImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
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
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Spy
    private MomentMapperImpl momentMapper;
    @Spy
    ProjectMapperProjectDtoImpl projectMapper;
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
    Project thirdProject;
    Project fourthProject;

    ProjectDto firstProjectDto;
    ProjectDto secondProjectDto;
    List<MomentDto> momentDtoList;
    List<Project> partnerProjectList = new ArrayList<>();
    List<Project> partnerProjectListForSecondMoment = new ArrayList<>();

    List<ProjectDto> partnerProjectDtoList = new ArrayList<>();

    List<Project> projectListToReturnFromDataBase = new ArrayList<>();

    Moment moment = new Moment();
    Moment secondMoment = new Moment();
    List<Moment> momentList;
    List<Long> projectIdList = new ArrayList<>();


    long id = 1;
    long firstProjectId = 3;
    long secondProjectId = 4;

    List<Long> userIdsMomentDto = new ArrayList<>();
    List<Long> userIdsMoment = new ArrayList<>();
    List<Long> userIdsForSecondMoment = new ArrayList<>();


    Team firstTeam = new Team();
    Team secondTeam = new Team();

    TeamMember firstMember = new TeamMember();
    TeamMember secondMember = new TeamMember();
    TeamMember thirdMember = new TeamMember();
    TeamMember fourthMember = new TeamMember();
    TeamMember fifthMember = new TeamMember();
    TeamMember sixthMember = new TeamMember();

    List<TeamMember> momentDtoTeamMemberList = new ArrayList<>();
    List<TeamMember> momentTeamMemberList = new ArrayList<>();
    List<TeamMember> secondMomentTeamMemberList = new ArrayList<>();
    List<TeamMember> teamMemberListToReturnFromDatabase = new ArrayList<>();
    ;


    @BeforeEach
    public void setupMomentDto() {
        momentDto.setId(id);
        momentDto.setName("Notification feature");
        momentDto.setDescription("MomentDto Description");
        momentDto.setCreatedAt(LocalDateTime.of(2023, 7, 15, 10, 30));
        firstProjectDto = new ProjectDto();
        secondProjectDto = new ProjectDto();
        firstProjectDto.setId(firstProjectId);
        secondProjectDto.setId(secondProjectId);
        projectIdList.add(firstProjectId);
        projectIdList.add(secondProjectId);
        firstProjectDto.setStatus(ProjectStatus.IN_PROGRESS);
        secondProjectDto.setStatus(ProjectStatus.IN_PROGRESS);
        partnerProjectDtoList.add(firstProjectDto);
        partnerProjectDtoList.add(secondProjectDto);
        momentDto.setProjects(partnerProjectDtoList);
        userIdsMomentDto.add(1L);
        userIdsMomentDto.add(2L);
        momentDto.setUserIds(userIdsMomentDto);
        firstMember.setUserId(1L);
        secondMember.setUserId(2L);

        firstMember.setTeam(firstTeam);
        secondMember.setTeam(secondTeam);

        firstTeam.setProject(thirdProject);
        secondTeam.setProject(fourthProject);

        momentDtoTeamMemberList.add(firstMember);
        momentDtoTeamMemberList.add(secondMember);
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
        momentDtoList.add(momentDto);
    }

    @BeforeEach
    public void setupMoment() {
        moment.setId(id);
        moment.setName("Notification feature");
        moment.setDescription("Moment description");
        moment.setCreatedAt(LocalDateTime.of(2023, 7, 15, 10, 30));
        firstProject = new Project();
        secondProject = new Project();
        firstProject.setStatus(ProjectStatus.IN_PROGRESS);
        secondProject.setStatus(ProjectStatus.IN_PROGRESS);

        partnerProjectList.add(firstProject);
        partnerProjectList.add(secondProject);
        moment.setProjects(partnerProjectList);
        userIdsMoment.add(3L);
        userIdsMoment.add(4L);
        moment.setUserIds(userIdsMoment);

        thirdMember.setUserId(1L);
        fourthMember.setUserId(2L);
        momentTeamMemberList.add(thirdMember);
        momentTeamMemberList.add(fourthMember);
    }

    @BeforeEach
    public void setupSecondMoment() {
        secondMoment.setId(id);
        secondMoment.setName("SecondMoment feature");
        secondMoment.setDescription("SecondMoment description");
        secondMoment.setCreatedAt(LocalDateTime.of(2023, 7, 15, 10, 30));
        thirdProject = new Project();
        thirdProject.setStatus(ProjectStatus.IN_PROGRESS);
        partnerProjectListForSecondMoment.add(thirdProject);
        secondMoment.setProjects(partnerProjectListForSecondMoment);
        userIdsForSecondMoment.add(5L);
        secondMoment.setUserIds(userIdsForSecondMoment);
        fifthMember.setUserId(5L);
        sixthMember.setUserId(6L);
        secondMomentTeamMemberList.add(fifthMember);
        secondMomentTeamMemberList.add(sixthMember);

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

    @BeforeEach
    public void setupGeneral() {
        projectListToReturnFromDataBase.add(firstProject);
        projectListToReturnFromDataBase.add(secondProject);
        projectListToReturnFromDataBase.add(thirdProject);
        projectListToReturnFromDataBase.add(fourthProject);

        teamMemberListToReturnFromDatabase.add(firstMember);
        teamMemberListToReturnFromDatabase.add(secondMember);
        teamMemberListToReturnFromDatabase.add(thirdMember);
        teamMemberListToReturnFromDatabase.add(fourthMember);
        teamMemberListToReturnFromDatabase.add(fifthMember);
        teamMemberListToReturnFromDatabase.add(sixthMember);

    }

    @Test
    public void createWithExistingMomentFailTest() {
        List<Moment> mappedMomentList = momentMapper.toEntityList(momentDtoList);
        when(momentRepository.findAll()).thenReturn(mappedMomentList);

        assertThrows(ValidationException.class, () -> momentService.create(momentDto));
    }

    @Test
    public void createWithProjectCompletedProjectStatusTest() {
        when(projectRepository.findAllByIds(projectIdList)).thenReturn(partnerProjectList);
        partnerProjectList.get(0).setStatus(ProjectStatus.COMPLETED);

        assertThrows(ValidationException.class, () -> momentService.create(momentDto));
    }

    @Test
    public void createWithProjectCancelledProjectStatusTest() {
        when(projectRepository.findAllByIds(projectIdList)).thenReturn(partnerProjectList);
        partnerProjectList.get(0).setStatus(ProjectStatus.CANCELLED);
        partnerProjectList.get(1).setStatus(ProjectStatus.IN_PROGRESS);

        assertThrows(ValidationException.class, () -> momentService.create(momentDto));
    }

    @Test
    public void createMomentSuccessTest() {
        Moment mappedMoment = momentMapper.toEntity(momentDto);
        when(momentRepository.save(mappedMoment)).thenReturn(mappedMoment);

        MomentDto savedMomentDto = momentService.create(momentDto);
        verify(momentRepository, times(1)).save(momentCaptor.capture());
        Moment capturedMoment = momentCaptor.getValue();
        assertEquals(mappedMoment, capturedMoment);
        assertEquals(momentDto, savedMomentDto);
    }

    @Test
    public void updateMomentSuccessTest() {
        when(momentMapper.toEntity(momentDto)).thenReturn(secondMoment);
        Optional<Moment> optionalMoment = Optional.of(moment);
        when(momentRepository.findById(id)).thenReturn(optionalMoment);
        when(projectRepository.findAll()).thenReturn(projectListToReturnFromDataBase);
        when(teamMemberJpaRepository.findAll()).thenReturn(teamMemberListToReturnFromDatabase);
        when(momentRepository.save(moment)).thenReturn(moment);
        MomentDto updatedMomentDto = momentService.update(momentDto);

        verify(momentRepository, times(1)).findById(longArgumentCaptor.capture());
        Long capturedId = longArgumentCaptor.getValue();
        assertEquals(secondMoment.getId(), capturedId);
        verify(projectRepository, times(1)).findAll();
        verify(teamMemberJpaRepository, times(1)).findAll();
        verify(momentRepository, times(1)).save(momentCaptor.capture());
        Moment capturedMoment = momentCaptor.getValue();
        assertEquals(moment, capturedMoment);
        assertEquals(secondMoment.getName(), moment.getName());
        assertEquals(momentMapper.toDto(moment), updatedMomentDto);

    }

    @Test
    public void getAllMomentsTest() {
        List<Moment> mappedMomentList = momentMapper.toEntityList(momentDtoList);
        when(momentRepository.findAll()).thenReturn(mappedMomentList);

        List<MomentDto> receivedMomentDtoList = momentService.getAllMoments();
        verify(momentRepository, times(1)).findAll();
        assertEquals(momentDtoList, receivedMomentDtoList);
    }

    @Test
    public void getMomentByIdWithPresentIdTest() {
        when(momentRepository.findById(id)).thenReturn(Optional.of(moment));

        MomentDto receivedMomentDto = momentService.getMomentById(id);
        verify(momentRepository, times(1)).findById(longArgumentCaptor.capture());
        long capturedId = longArgumentCaptor.getValue();
        assertEquals(id, capturedId);
        assertEquals(momentMapper.toDto(moment), receivedMomentDto);
    }

    @Test
    public void getMomentByIdWithAbsentIdTest() {
        Optional<Moment> optionalMoment = Optional.empty();
        when(momentRepository.findById(id)).thenReturn(optionalMoment);

        assertThrows(ValidationException.class, () -> momentService.getMomentById(id));
    }

    @Test
    public void getMomentsByFilterTest() {
        when(momentRepository.findAll()).thenReturn(momentList);

        List<MomentDto> receivedMomentDtoList = momentService.getMomentsByFilter(momentFilterDto);
        verify(momentRepository, times(1)).findAll();
        assertEquals(momentList, momentMapper.toEntityList(receivedMomentDtoList));
    }

}
