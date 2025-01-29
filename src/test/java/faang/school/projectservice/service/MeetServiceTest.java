package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.meet.MeetCreateRequest;
import faang.school.projectservice.dto.meet.MeetFilterRequest;
import faang.school.projectservice.dto.meet.MeetResponse;
import faang.school.projectservice.dto.meet.MeetUpdateRequest;
import faang.school.projectservice.exception.MeetingOwnershipRequiredException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.filter.meet.MeetFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    @Mock
    private UserValidator userValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private MeetMapper meetMapper = Mappers.getMapper(MeetMapper.class);

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private List<MeetFilter> meetFilters = new ArrayList<>();

    @InjectMocks
    private MeetService meetService;

    @Captor
    private ArgumentCaptor<Meet> meetCaptor;

    @Test
    void createMeet_Success() {
        Meet savedMeet = new Meet();
        savedMeet.setId(100L);
        savedMeet.setCreatorId(1L);
        savedMeet.setProject(Project.builder().id(10L).build());

        willDoNothing().given(userValidator).validateUser(1L);
        willDoNothing().given(userValidator).validateUsers(List.of(2L, 3L));
        willDoNothing().given(projectValidator).validateProject(10L);
        when(meetRepository.save(any(Meet.class))).thenReturn(savedMeet);

        MeetCreateRequest request = MeetCreateRequest.builder()
                .creatorId(1L)
                .title("title")
                .description("description")
                .projectId(10L)
                .userIds(List.of(2L, 3L))
                .build();

        MeetResponse response = meetService.createMeet(request);
        verify(meetRepository).save(meetCaptor.capture());
        assertEquals(100L, response.id());

        Meet captured = meetCaptor.getValue();
        assertEquals(1L, captured.getCreatorId());
        assertEquals(10L, captured.getProject().getId());
    }

    @Test
    void createMeet_WithNullUsers() {
        MeetCreateRequest request = MeetCreateRequest.builder()
                .creatorId(1L)
                .title("title")
                .description("description")
                .projectId(10L)
                .userIds(null)
                .build();

        Meet savedMeet = new Meet();
        savedMeet.setId(101L);

        when(meetRepository.save(any(Meet.class))).thenReturn(savedMeet);

        MeetResponse response = meetService.createMeet(request);

        verify(meetRepository).save(any(Meet.class));
        assertEquals(101L, response.id());
    }

    @Test
    void updateMeet_Success() {
        Meet existingMeet = new Meet();
        existingMeet.setId(100L);
        existingMeet.setCreatorId(1L);
        existingMeet.setTitle("old description");

        UserDto userDto = UserDto.builder().id(1L).build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(meetRepository.findById(100L)).thenReturn(Optional.of(existingMeet));

        MeetUpdateRequest request = MeetUpdateRequest.builder()
                .meetId(100L)
                .title("new title")
                .description("new description")
                .userId(1L)
                .build();

        MeetResponse response = meetService.updateMeet(request);

        assertEquals("new title", response.title());
        assertEquals("new description", response.description());
    }

    @Test
    void updateMeet_NotOwnerThrowsException() {
        Meet existingMeet = new Meet();
        existingMeet.setId(100L);
        existingMeet.setCreatorId(1L);

        UserDto userDto = UserDto.builder().id(2L).build();

        when(meetRepository.findById(100L)).thenReturn(Optional.of(existingMeet));
        when(userServiceClient.getUser(2L)).thenReturn(userDto);

        MeetUpdateRequest request = MeetUpdateRequest.builder()
                .meetId(100L)
                .title("new title")
                .description("new description")
                .userId(2L)
                .build();

        assertThrows(MeetingOwnershipRequiredException.class, () -> meetService.updateMeet(request));
    }

    @Test
    void deleteMeet_Success() {
        when(meetRepository.existsById(100L)).thenReturn(true);
        when(meetRepository.isUserOwnerMeet(100L, 1L)).thenReturn(true);

        meetService.deleteMeet(100L, 1L);

        verify(meetRepository).deleteById(100L);
    }

    @Test
    void deleteMeet_NotFoundThrowsException() {
        when(meetRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> meetService.deleteMeet(999L, 1L));
        verify(meetRepository, never()).deleteById(any());
    }

    @Test
    void deleteMeetNotOwnerThrowsException() {
        when(meetRepository.existsById(100L)).thenReturn(true);
        when(meetRepository.isUserOwnerMeet(100L, 1L)).thenReturn(false);

        assertThrows(MeetingOwnershipRequiredException.class, () -> meetService.deleteMeet(100L, 1L));
        verify(meetRepository, never()).deleteById(any());
    }

    @Test
    void getMeetById_Success() {
        Meet meet = new Meet();
        meet.setId(100L);

        when(meetRepository.findById(100L)).thenReturn(Optional.of(meet));

        MeetResponse response = meetService.getMeetById(100L);

        assertNotNull(response);
        assertEquals(100L, response.id());
    }

    @Test
    void getMeetById_NotFoundThrowsException() {
        when(meetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetService.getMeetById(999L));
    }

    @Test
    void getMeetsByFilter() {
        Meet meet1 = new Meet();
        meet1.setId(1L);
        meet1.setProject(Project.builder().id(10L).build());

        Meet meet2 = new Meet();
        meet2.setId(2L);
        meet2.setProject(Project.builder().id(10L).build());

        when(meetRepository.findByProjectId(10L))
                .thenReturn(Stream.of(meet1, meet2));

        MeetFilterRequest filterRequest = new MeetFilterRequest(null, 10L, null);

        List<MeetResponse> responses = meetService.getMeetsByFilter(filterRequest);

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals(2L, responses.get(1).id());
    }

    @Test
    void testGetMeetsByFilterWithFilters() {
        Meet meet1 = new Meet();
        meet1.setId(1L);
        meet1.setProject(Project.builder().id(10L).build());
        meet1.setTitle("Тема1");

        Meet meet2 = new Meet();
        meet2.setId(2L);
        meet2.setProject(Project.builder().id(10L).build());
        meet2.setTitle("Тема2");

        when(meetRepository.findByProjectId(10L))
                .thenReturn(Stream.of(meet1, meet2));

        MeetFilterRequest filterRequest = MeetFilterRequest.builder().projectId(10L).title("Тема").build();

        // Допустим, у нас есть один мок-фильтр, который пропускает только meet1
        MeetFilter filterMock = mock(MeetFilter.class);
        when(filterMock.filter(any(Stream.class), eq(filterRequest)))
                .thenAnswer(invocation -> {
                    Stream<Meet> streamArg = invocation.getArgument(0);
                    return streamArg.filter(m -> m.getTitle().equals("Тема1"));
                });
        List<MeetFilter> filters = new ArrayList<>();
        filters.add(filterMock);


        when(meetFilters.iterator()).thenReturn(filters.iterator());

        List<MeetResponse> responses = meetService.getMeetsByFilter(filterRequest);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).id());
    }

    @Test
    void testGetMeetsByProjectId() {
        Long projectId = 10L;
        Meet meet1 = new Meet();
        meet1.setId(1L);
        meet1.setProject(Project.builder().id(projectId).build());

        when(meetRepository.findByProjectId(projectId))
                .thenReturn(Stream.of(meet1));

        List<MeetResponse> responses = meetService.getMeetsByProjectId(projectId);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).id());
    }

}