package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.MeetingValidator;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
    private UserServiceClient userServiceClient;

    @Mock
    private List<Filter<Meet, MeetDto>> meetFilters;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private MeetingValidator meetingValidator;

    @InjectMocks
    private MeetingService meetingService;

    private MeetDto meetDto;

    @BeforeEach
    public void setup() {
        meetDto = createMeetDto(1L, "Team meeting", MeetStatus.PENDING);
    }


    @Test
    void testCreateMeetingWhenUserExists() {
        Meet meetEntity = createMeet(1L, "Team meeting", MeetStatus.PENDING);
        Meet savedMeet = createMeet(1L, "Team meeting", MeetStatus.PENDING);

        when(userServiceClient.getUser(anyLong())).thenReturn(new UserDto());
        when(meetMapper.toMeet(meetDto)).thenReturn(meetEntity);
        when(meetRepository.save(meetEntity)).thenReturn(savedMeet);
        when(meetMapper.toDto(savedMeet)).thenReturn(meetDto);

        MeetDto result = meetingService.createMeeting(meetDto);

        assertNotNull(result);
        assertEquals("Team meeting", result.getTitle());

        verify(userServiceClient).getUser(1L);
        verify(meetRepository).save(meetEntity);
        verify(meetMapper).toMeet(meetDto);
        verify(meetMapper).toDto(savedMeet);
    }

    @Test
    void testCreateMeetingUserNotFound() {
        when(userServiceClient.getUser(anyLong())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> meetingService.createMeeting(meetDto)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userServiceClient).getUser(1L);
        verify(meetRepository, never()).save(any());
    }

    @Test
    void testUpdateMeetingWithValidData() {
        long meetId = 1L;

        Meet meet = createMeet(meetId, "Team meeting", MeetStatus.COMPLETED);
        MeetDto updateDto = createMeetDto(meetId, "Team meeting", MeetStatus.COMPLETED);

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(any(Meet.class))).thenAnswer(invocation -> {
            Meet updatedMeet = invocation.getArgument(0);
            return createMeetDto(updatedMeet.getId(), updatedMeet.getTitle(), updatedMeet.getStatus());
        });

        MeetDto result = meetingService.updateMeeting(updateDto, meetId);

        assertNotNull(result);
        assertEquals(MeetStatus.COMPLETED, result.getMeetStatus());
        assertEquals("Team meeting", result.getTitle());

        verify(meetRepository).findById(meetId);
        verify(meetMapper).toDto(meet);
    }

    @Test
    void testUpdateMeetingWhenMeetingCancelled() {
        long meetId = 1L;
        Meet meet = createMeet(meetId, "Cancelled meeting", MeetStatus.CANCELLED);
        meetDto.setMeetStatus(MeetStatus.CANCELLED);

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));

        doThrow(new RuntimeException("Meeting is already cancelled"))
                .when(meetingValidator)
                .validateMeetingUpdate(any(MeetDto.class), anyLong(), any(Meet.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> meetingService.updateMeeting(meetDto, meetId)
        );
        assertEquals("Meeting is already cancelled", exception.getMessage());
        verify(meetRepository).findById(meetId);
        verify(meetRepository, never()).save(any());
    }

    @Test
    void testDeleteMeetingWhenUserIsProjectOwner() {
        long meetId = 1L;
        long projectId = 1L;
        long currentUserId = 1L;

        Project project = new Project();
        project.setId(projectId);
        project.setOwnerId(currentUserId);

        when(projectService.getProjectById(projectId)).thenReturn(project);

        doNothing().when(projectValidator).checkUserIsProjectOwner(eq(projectId), eq(currentUserId), eq(project));

        meetingService.deleteMeeting(projectId, currentUserId, meetId);

        verify(projectValidator, times(1)).checkUserIsProjectOwner(eq(projectId), eq(currentUserId), eq(project));
        verify(meetRepository).deleteById(meetId);
    }

    @Test
    void testGetAllMeetings() {
        Meet meet1 = createMeet(1L, "Team meeting", MeetStatus.PENDING);
        Meet meet2 = createMeet(2L, "Project meeting", MeetStatus.COMPLETED);

        List<Meet> meetList = List.of(meet1, meet2);

        MeetDto meetDto1 = createMeetDto(1L, "Team meeting", MeetStatus.PENDING);
        MeetDto meetDto2 = createMeetDto(2L, "Project meeting", MeetStatus.COMPLETED);

        when(meetRepository.findAll()).thenReturn(meetList);
        when(meetMapper.toDto(meet1)).thenReturn(meetDto1);
        when(meetMapper.toDto(meet2)).thenReturn(meetDto2);

        List<MeetDto> result = meetingService.getAllMeetings();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Team meeting", result.get(0).getTitle());
        assertEquals("Project meeting", result.get(1).getTitle());

        verify(meetRepository).findAll();
        verify(meetMapper, times(2)).toDto(any(Meet.class));
    }

    private MeetDto createMeetDto(long id, String title, MeetStatus status) {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(id);
        meetDto.setTitle(title);
        meetDto.setMeetStatus(status);
        meetDto.setCreatorId(1L);
        meetDto.setProjectId(1L);
        meetDto.setCreatedAt(LocalDateTime.now());
        meetDto.setUpdatedAt(LocalDateTime.now().plusDays(1));
        return meetDto;
    }

    private Meet createMeet(long id, String title, MeetStatus status) {
        Meet meet = new Meet();
        meet.setId(id);
        meet.setTitle(title);
        meet.setStatus(status);
        meet.setCreatorId(1L);
        return meet;
    }

    private Project createProject(long id, long ownerId) {
        Project project = new Project();
        project.setId(id);
        project.setOwnerId(ownerId);
        return project;
    }
}
