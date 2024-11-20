package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.MeetValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {

    @InjectMocks
    private MeetService meetService;
    @Mock
    private MeetRepository meetRepository;
    @Spy
    private MeetMapper meetMapper = Mappers.getMapper(MeetMapper.class);
    @Mock
    private ProjectService projectService;
    @Mock
    private MeetValidator meetValidator;
    @Mock
    private HttpServletRequest request;

    private Meet meet;
    private List<Meet> meets;
    private MeetDto meetDto;
    private Project project;
    private UpdateMeetDto updateMeetDto;
    private List<Meet> meetList;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .id(1L)
                .name("project")
                .build();
        meet = new Meet();
        meet.setId(1L);
        meet.setCreatorId(1L);
        List<Long> userIds = new ArrayList<>() {{
            add(1L);
            add(2L);
            add(3L);
        }};
        meet.setUserIds(userIds);
        meetDto = MeetDto.builder()
                .id(1L)
                .title("title")
                .description("desc")
                .userIds(List.of(1L, 2L, 3L))
                .creatorId(1L)
                .projectId(project.getId())
                .status(MeetStatus.PENDING)
                .build();
        updateMeetDto = UpdateMeetDto.builder()
                .id(1L)
                .title("new title")
                .description("new desc")
                .userIds(List.of(1L, 2L))
                .status(MeetStatus.COMPLETED)
                .build();
        Meet secondMeet = new Meet();
        secondMeet.setId(2L);
        secondMeet.setTitle("any title");
        secondMeet.setCreatedAt(LocalDateTime.now());
        meetList = List.of(meet, secondMeet);
    }

    @Test
    public void testCreateMeet() {
        when(projectService.findProjectById(project.getId())).thenReturn(project);
        when(meetRepository.save(any())).thenReturn(meet);

        meetService.createMeet(meetDto);

        verify(projectService, times(1)).findProjectById(project.getId());
        verify(meetRepository, times(1)).save(any());
        verify(meetValidator, times(1)).validateUserExists(meetDto.creatorId());
    }

    @Test
    public void testUpdateMeet() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));

        MeetDto dto = meetService.updateMeet(updateMeetDto, request);

        verify(meetRepository, times(1)).findById(anyLong());
        verify(meetValidator, times(1)).validateMeetUpdating(meet);
        verify(meetValidator, times(1)).validateThatRequestWasSentByTheCreator(meet, request);
        assertEquals(dto.description(), updateMeetDto.description());
        assertEquals(dto.userIds(), updateMeetDto.userIds());
        assertEquals(dto.description(), updateMeetDto.description());
        assertEquals(dto.status(), updateMeetDto.status());
    }

    @Test
    public void testDeleteMeetingParticipant() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        Long userId = 1L;

        MeetDto dto = meetService.deleteMeetingParticipant(1L, userId, request);

        verify(meetRepository, times(1)).findById(anyLong());
        verify(meetValidator, times(1)).validateThatRequestWasSentByTheCreator(meet, request);
        verify(meetValidator, times(1)).validateParticipants(userId, meet);
        verify(meetRepository, times(1)).findById(userId);
        assertFalse(dto.userIds().contains(userId));
    }

    @Test
    public void testCancelMeet() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));

        MeetDto dto = meetService.cancelMeet(1L, request);

        verify(meetRepository, times(1)).findById(anyLong());
        verify(meetValidator, times(1)).validateThatRequestWasSentByTheCreator(meet, request);
        assertEquals(dto.status(), MeetStatus.CANCELLED);
    }

    @Test
    public void testGetMeets() {
        when(meetRepository.findAll()).thenReturn(meetList);
        meetService.getMeets();
        verify(meetRepository, times(1)).findAll();
    }

    @Test
    public void testGetMeetById() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        meetService.getMeetById(1L);
        verify(meetRepository, times(1)).findById(anyLong());
    }


}
