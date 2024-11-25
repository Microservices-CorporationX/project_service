package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.MeetingValidator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private MeetingValidator meetingValidator;

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
        when(meetRepository.save(meet)).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto createdMeeting = meetingService.createMeeting(meetDto);

        assertNotNull(createdMeeting);
        assertEquals(meetDto.getId(), createdMeeting.getId());
        verify(meetRepository, times(1)).save(meet);
    }

    @Test
    void testUpdateMeeting() {
        when(meetRepository.findById(meetDto.getId())).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto updatedMeeting = meetingService.updateMeeting(meetDto, meetDto.getId());

        assertNotNull(updatedMeeting);
        assertEquals(meetDto.getId(), updatedMeeting.getId());
        verify(meetRepository, times(1)).findById(meetDto.getId());
    }

    @Test
    void testDeleteMeeting() {
        meetingService.deleteMeeting(project.getId(), meetDto.getCreatorId(), meetDto.getId());

        verify(meetRepository, times(1)).deleteById(meetDto.getId());
    }

    @Test
    void testFilterMeetings() {
        when(meetRepository.findAll()).thenReturn(List.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        List<MeetDto> filteredMeetings = meetingService.filterMeetings(meetDto);

        assertNotNull(filteredMeetings);
        assertFalse(filteredMeetings.isEmpty());
        assertEquals(1, filteredMeetings.size());
    }
}
