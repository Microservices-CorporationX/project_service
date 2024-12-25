package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.MeetingAlreadyCancelledException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.meet.MeetDateFilter;
import faang.school.projectservice.filter.meet.MeetNameFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {
    @Mock
    private MeetRepository meetRepository;

    @Mock
    private MeetMapper meetMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private List<Filter<Meet, MeetDto>> meetFilters;

    @InjectMocks
    private MeetingService meetingService;

    private MeetDto meetDto;
    private Meet meet;
    private Project project;

    @BeforeEach
    void setUp() {
        meetDto = MeetDto.builder()
                .id(1L)
                .creatorId(1L)
                .projectId(2L)
                .title("Meeting 1")
                .description("Description")
                .createdAt(LocalDateTime.now())
                .build();

        meet = new Meet();
        meet.setId(1L);
        meet.setCreatorId(1L);
        meet.setProject(project);
        meet.setTitle("Meeting 1");
        meet.setDescription("Description");
        meet.setCreatedAt(LocalDateTime.now());

        project = new Project();
        project.setId(1L);
    }

    @Test
    void testCreateMeeting() {
        doNothing().when(projectValidator).validateProjectExistsById(meetDto.getProjectId());
        doNothing().when(teamMemberService).validateInvitedUsersExistInTeam(meetDto);

        when(meetMapper.toMeet(meetDto)).thenReturn(meet);
        when(projectService.getProjectById(meetDto.getProjectId())).thenReturn(project);
        when(meetRepository.save(meet)).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto createdMeeting = meetingService.createMeeting(meetDto);

        assertNotNull(createdMeeting);
        assertEquals(meetDto.getId(), createdMeeting.getId());
        verify(meetRepository, times(1)).save(meet);
        verify(projectService, times(1)).getProjectById(meetDto.getProjectId());
    }

    @Test
    void testUpdateMeeting() {
        meet.setStatus(MeetStatus.COMPLETED);
        meetDto.setMeetStatus(MeetStatus.CANCELLED);

        when(meetRepository.findById(meetDto.getId())).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto updatedMeeting = meetingService.updateMeeting(meetDto, meetDto.getId());

        assertNotNull(updatedMeeting);
        assertEquals(MeetStatus.CANCELLED, meet.getStatus());
        verify(meetRepository, times(1)).findById(meetDto.getId());
    }

    @Test
    void testUpdateMeetingAlreadyCancelled() {
        meet.setStatus(MeetStatus.CANCELLED);
        meetDto.setMeetStatus(MeetStatus.CANCELLED);

        when(meetRepository.findById(meetDto.getId())).thenReturn(Optional.of(meet));

        assertThrows(MeetingAlreadyCancelledException.class, () -> meetingService.updateMeeting(meetDto, meetDto.getId()));
    }

    @Test
    void testDeleteMeeting() {
        long creatorId = 1L;
        long meetingId = 1L;

        meet.setCreatorId(meetDto.getCreatorId());

        when(meetRepository.findById(meetDto.getId())).thenReturn(Optional.of(meet));

        meetingService.deleteMeeting(meetingId, creatorId);

        verify(meetRepository, times(1)).deleteById(meetDto.getId());
    }

    @Test
    void testDeleteMeetingUnauthorized() {
        meet.setCreatorId(999L);

        when(meetRepository.findById(meetDto.getId())).thenReturn(Optional.of(meet));

        assertThrows(UnauthorizedAccessException.class, () -> meetingService.deleteMeeting(meetDto.getId(), anyLong()));
    }

    @Test
    void testFilterMeetings() {
        when(meetRepository.findAll()).thenReturn(List.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        Stream<Meet> filteredStream = Stream.of(meet);
        when(meetFilters.stream()).thenReturn(Stream.empty());

        List<MeetDto> filteredMeetings = meetingService.filterMeetings(meetDto);

        assertNotNull(filteredMeetings);
        assertFalse(filteredMeetings.isEmpty());
        assertEquals(1, filteredMeetings.size());
        verify(meetRepository, times(1)).findAll();
    }

    @Test
    void testFilterMeetingsWithoutApplicableFilters() {
        Meet meet = new Meet();
        MeetDto meetDto = new MeetDto();

        when(meetRepository.findAll()).thenReturn(List.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);


        Filter<Meet, MeetDto> filter1 = mock(Filter.class);
        Filter<Meet, MeetDto> filter2 = mock(Filter.class);

        when(filter1.isApplicable(meetDto)).thenReturn(false);
        when(filter2.isApplicable(meetDto)).thenReturn(false);

        when(meetFilters.stream()).thenReturn(Stream.of(filter1, filter2));

        List<MeetDto> result = meetingService.filterMeetings(meetDto);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "Must be one result");
        assertEquals(meetDto, result.get(0), "Result must contains meetDto");

        verify(meetRepository, times(1)).findAll();
        verify(filter1, times(1)).isApplicable(meetDto);
        verify(filter2, times(1)).isApplicable(meetDto);
        verifyNoMoreInteractions(filter1, filter2);
    }

    @Test
    void testGetMeetingById() {
        when(meetRepository.findById(meetDto.getId())).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto foundMeeting = meetingService.getMeetingById(meetDto.getId());

        assertNotNull(foundMeeting);
        assertEquals(meetDto.getId(), foundMeeting.getId());
        verify(meetRepository, times(1)).findById(meetDto.getId());
    }
}
