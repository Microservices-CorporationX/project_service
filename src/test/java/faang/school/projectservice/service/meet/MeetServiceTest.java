package faang.school.projectservice.service.meet;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.teammember.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private MeetMapper meetMapper;

    @InjectMocks
    private MeetService meetService;

    private static final String UNAUTHORIZED_ACCESS_TO_UPDATE_MESSAGE = "Only the creator can update the meeting";
    private static final String UNAUTHORIZED_ACCESS_TO_DELETE_MESSAGE = "Only the creator can delete the meeting";

    @Test
    void testCreateMeetingSuccessful() {
        MeetDto meetDto = createMeetDto(null, "Meet title", "Meet description", null, 1L,
                1L, List.of(2L, 3L), LocalDateTime.of(2024, 12, 12, 10, 30));
        Meet meetToSave = createMeet("Meet title", "Meet description", null, 1L,
                null, List.of(2L, 3L), LocalDateTime.of(2024, 12, 12, 10, 30));
        Project project = createProject(meetDto.getProjectId());
        Meet savedMeet = createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                project, List.of(2L, 3L), LocalDateTime.of(2024, 12, 12, 10, 30));
        MeetDto meetDtoResult = createMeetDto(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                1L, List.of(2L, 3L), LocalDateTime.of(2024, 12, 12, 10, 30));
        UserDto creator = createUserDto(meetDto.getCreatorId());
        List<UserDto> participants = List.of(createUserDto(2L), createUserDto(3L));

        when(projectRepository.getProjectById(meetDto.getProjectId())).thenReturn(project);
        when(userServiceClient.getUser(meetDto.getCreatorId())).thenReturn(creator);
        when(userServiceClient.getUsersByIds(new ArrayList<>(meetDto.getUserIds()))).thenReturn(participants);
        when(teamMemberService.isUserInAnyTeamOfProject(meetDto.getProjectId(), meetDto.getCreatorId())).thenReturn(true);
        when(teamMemberService.areAllUsersInProjectTeams(meetDto.getProjectId(), meetDto.getUserIds())).thenReturn(true);
        when(meetMapper.toEntity(meetDto)).thenReturn(meetToSave);
        when(meetRepository.save(meetToSave)).thenReturn(savedMeet);
        when(meetMapper.toDto(savedMeet)).thenReturn(meetDtoResult);

        MeetDto result = meetService.createMeeting(meetDto);

        verify(projectRepository, times(1)).getProjectById(meetDto.getProjectId());
        verify(userServiceClient, times(1)).getUser(meetDto.getCreatorId());
        verify(userServiceClient, times(1)).getUsersByIds(meetDto.getUserIds());
        verify(teamMemberService, times(1)).isUserInAnyTeamOfProject(meetDto.getProjectId(), meetDto.getCreatorId());
        verify(teamMemberService, times(1)).areAllUsersInProjectTeams(meetDto.getProjectId(), meetDto.getUserIds());
        verify(meetMapper, times(1)).toEntity(meetDto);
        verify(meetRepository, times(1)).save(meetToSave);
        verify(meetMapper, times(1)).toDto(savedMeet);

        assertEquals(meetDtoResult, result);
    }

    @Test
    void testUpdateMeetingSuccessful() {
        Long meetId = 1L;
        Long userId = 1L;
        MeetDto meetDtoForUpdate = createMeetDto(1L, "Updated meet title", "Updated meet description", MeetStatus.PENDING, 1L,
                1L, List.of(2L, 4L), LocalDateTime.of(2024, 12, 12, 11, 30));
        Meet meetToUpdate = createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                createProject(1L), List.of(2L, 3L), LocalDateTime.of(2024, 12, 12, 10, 30));
        List<UserDto> participants = List.of(createUserDto(2L), createUserDto(4L));
        Meet updatedMeet = createMeet(1L, "Updated meet title", "Updated meet description", MeetStatus.PENDING, 1L,
                createProject(1L), List.of(2L, 4L), LocalDateTime.of(2024, 12, 12, 11, 30));
        MeetDto updatedMeetDto = createMeetDto(1L, "Updated meet title", "Updated meet description", MeetStatus.PENDING, 1L,
                1L, List.of(2L, 4L), LocalDateTime.of(2024, 12, 12, 11, 30));

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meetToUpdate));
        when(userServiceClient.getUsersByIds(new ArrayList<>(meetDtoForUpdate.getUserIds()))).thenReturn(participants);
        when(teamMemberService.areAllUsersInProjectTeams(meetDtoForUpdate.getProjectId(), meetDtoForUpdate.getUserIds())).thenReturn(true);
        when(meetRepository.save(any(Meet.class))).thenReturn(updatedMeet);
        when(meetMapper.toDto(updatedMeet)).thenReturn(updatedMeetDto);

        MeetDto result = meetService.updateMeeting(meetId, userId, meetDtoForUpdate);

        verify(meetRepository, times(1)).findById(meetId);
        verify(userServiceClient, times(1)).getUsersByIds(meetDtoForUpdate.getUserIds());
        verify(teamMemberService, times(1)).areAllUsersInProjectTeams(meetDtoForUpdate.getProjectId(), meetDtoForUpdate.getUserIds());
        verify(meetMapper, times(1)).updateEntity(meetDtoForUpdate, meetToUpdate);
        verify(meetRepository, times(1)).save(any(Meet.class));
        verify(meetMapper, times(1)).toDto(updatedMeet);

        assertEquals(updatedMeetDto, result);
    }

    @Test
    void testUpdateMeetingThrowsUnauthorizedAccessException() {
        Long meetId = 1L;
        Long userId = 2L;
        MeetDto meetDtoForUpdate = createMeetDto(1L, "Updated meet title", "Updated meet description", MeetStatus.PENDING, 1L,
                1L, List.of(2L, 4L), LocalDateTime.of(2024, 12, 12, 11, 30));
        Meet meetToUpdate = createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                createProject(1L), List.of(2L, 3L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meetToUpdate));

        UnauthorizedAccessException unauthorizedAccessException = assertThrows(UnauthorizedAccessException.class, () ->
                meetService.updateMeeting(meetId, userId, meetDtoForUpdate));

        verify(meetRepository, times(1)).findById(meetId);

        assertEquals(UNAUTHORIZED_ACCESS_TO_UPDATE_MESSAGE, unauthorizedAccessException.getMessage());
    }

    @Test
    void testDeleteMeetingSuccessful() {
        Long meetId = 1L;
        Long userId = 1L;
        Meet meet = createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                createProject(1L), List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));

        meetService.deleteMeeting(meetId, userId);

        verify(meetRepository, times(1)).findById(meetId);
        verify(meetRepository, times(1)).deleteById(meetId);
    }

    @Test
    void testDeleteMeetingThrowsUnauthorizedAccessException() {
        Long meetId = 1L;
        Long userId = 2L;
        Meet meet = createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                createProject(1L), List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));

        UnauthorizedAccessException unauthorizedAccessException = assertThrows(UnauthorizedAccessException.class, () ->
                meetService.deleteMeeting(meetId, userId));

        verify(meetRepository, times(1)).findById(meetId);
        verify(meetRepository, never()).deleteById(meetId);

        assertEquals(UNAUTHORIZED_ACCESS_TO_DELETE_MESSAGE, unauthorizedAccessException.getMessage());
    }

    @Test
    void testGetMeetingsByProjectFilteredByDateOrTitleSuccessful() {
        Long projectId = 1L;
        String title = "Meet title";
        LocalDateTime dateFrom = LocalDateTime.of(2024, 11, 12, 10, 30);
        LocalDateTime dateTo = LocalDateTime.of(2024, 12, 13, 10, 30);

        List<Meet> meets = List.of(
                createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                        createProject(1L), List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30))
        );
        List<MeetDto> meetDtos = List.of(
                createMeetDto(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                        1L, List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30))
        );

        when(meetRepository.findAll(any(Specification.class))).thenReturn(meets);
        when(meetMapper.toDto(meets)).thenReturn(meetDtos);

        List<MeetDto> result = meetService.getMeetingsByProjectFilteredByDateOrTitle(projectId, title, dateFrom, dateTo);

        verify(meetRepository, times(1)).findAll(any(Specification.class));
        verify(meetMapper, times(1)).toDto(meets);

        assertEquals(meetDtos, result);
    }

    @Test
    void testGetAllMeetingsSuccessful() {
        List<Meet> meets = List.of(
                createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                        createProject(1L), List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30))
        );
        List<MeetDto> meetDtos = List.of(
                createMeetDto(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                        1L, List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30))
        );

        when(meetRepository.findAll()).thenReturn(meets);
        when(meetMapper.toDto(meets)).thenReturn(meetDtos);

        List<MeetDto> result = meetService.getAllMeetings();

        verify(meetRepository, times(1)).findAll();
        verify(meetMapper, times(1)).toDto(meets);

        assertEquals(meetDtos, result);
    }

    @Test
    void testGetMeetingByIdSuccessful() {
        Long meetId = 1L;
        Meet meet = createMeet(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                createProject(1L), List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30));
        MeetDto meetDto = createMeetDto(1L, "Meet title", "Meet description", MeetStatus.PENDING, 1L,
                1L, List.of(2L), LocalDateTime.of(2024, 12, 12, 10, 30));

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        MeetDto result = meetService.getMeetingById(meetId);

        verify(meetRepository, times(1)).findById(meetId);
        verify(meetMapper, times(1)).toDto(meet);

        assertEquals(meetDto, result);
    }

    private MeetDto createMeetDto(Long id, String title, String description, MeetStatus status, Long creatorId,
                                  Long projectId, List<Long> userIds, LocalDateTime meetDate) {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(id);
        meetDto.setTitle(title);
        meetDto.setDescription(description);
        meetDto.setStatus(status);
        meetDto.setCreatorId(creatorId);
        meetDto.setProjectId(projectId);
        meetDto.setUserIds(userIds);
        meetDto.setMeetDate(meetDate);
        return meetDto;
    }

    private Meet createMeet(Long id, String title, String description, MeetStatus status, Long creatorId,
                            Project project, List<Long> userIds, LocalDateTime meetDate) {
        Meet meet = new Meet();
        meet.setId(id);
        meet.setTitle(title);
        meet.setDescription(description);
        meet.setStatus(status);
        meet.setCreatorId(creatorId);
        meet.setProject(project);
        meet.setUserIds(userIds);
        meet.setMeetDate(meetDate);
        return meet;
    }

    private Meet createMeet(String title, String description, MeetStatus status, Long creatorId,
                            Project project, List<Long> userIds, LocalDateTime meetDate) {
        Meet meet = new Meet();
        meet.setTitle(title);
        meet.setDescription(description);
        meet.setStatus(status);
        meet.setCreatorId(creatorId);
        meet.setProject(project);
        meet.setUserIds(userIds);
        meet.setMeetDate(meetDate);
        return meet;
    }

    private Project createProject(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    private UserDto createUserDto(Long userId) {
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        return userDto;
    }
}
