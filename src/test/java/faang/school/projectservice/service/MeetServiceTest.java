package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private UserServiceClient userServiceClient;
    @Mock
    private MeetRepository meetRepository;
    @Spy
    private MeetMapper meetMapper = Mappers.getMapper(MeetMapper.class);
    @Mock
    private ProjectService projectService;
    @Mock
    private UserContext userContext;

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
        meet.setProject(project);
        meet.setStatus(MeetStatus.PENDING);
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
    }

    @Test
    public void testCreateMeetWithNonexistentUser() {
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.class);
        assertThrows(EntityNotFoundException.class, () -> meetService.createMeet(meetDto));
    }

    @Test
    public void testUpdateMeet() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(1L);

        MeetDto dto = meetService.updateMeet(1L, updateMeetDto);

        verify(meetRepository, times(1)).findById(anyLong());
        verify(userContext, times(1)).getUserId();
        assertEquals(dto.description(), updateMeetDto.description());
        assertEquals(dto.userIds(), updateMeetDto.userIds());
        assertEquals(dto.description(), updateMeetDto.description());
        assertEquals(dto.status(), updateMeetDto.status());
    }

    @Test
    public void testUpdateAlreadyCancelledMeet() {
        meet.setStatus(MeetStatus.CANCELLED);
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        assertThrows(DataValidationException.class, () -> meetService.updateMeet(1L, updateMeetDto));
    }

    @Test
    public void testUpdateMeetByNotCreatorUser() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(anyLong());

        assertThrows(DataValidationException.class, () -> meetService.updateMeet(1L, updateMeetDto));
        verify(meetRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateMeetByNonExistenceUser() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.class);

        assertThrows(EntityNotFoundException.class, () -> meetService.updateMeet(1L, updateMeetDto));

        verify(meetRepository, times(1)).findById(anyLong());
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void testDeleteMeetingParticipant() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(1L);
        Long userId = 1L;

        MeetDto dto = meetService.deleteMeetingParticipant(1L, userId);

        verify(meetRepository, times(1)).findById(anyLong());
        verify(userContext, times(1)).getUserId();
        verify(meetRepository, times(1)).findById(userId);
        assertFalse(dto.userIds().contains(userId));
    }

    @Test
    public void testDeleteMeetingNotParticipant() {
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(99999L);

        assertThrows(DataValidationException.class, () -> meetService.deleteMeetingParticipant(1L, 99999L));

        verify(meetRepository, times(1)).findById(anyLong());
        verify(userContext, times(1)).getUserId();
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
