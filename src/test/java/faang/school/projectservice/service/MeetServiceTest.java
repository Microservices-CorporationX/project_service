package faang.school.projectservice.service;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {

    @Mock
    private MeetRepository meetRepository;

    @InjectMocks
    private MeetService meetService;

    @Test
    public void testGetMeetsByIds_ValidIds() {
        Meet meet1 = new Meet();
        meet1.setId(1L);
        meet1.setTitle("Meeting 1");
        meet1.setDescription("Description 1");
        meet1.setCreatorId(101L);
        meet1.setStartsAt(LocalDateTime.now());

        Meet meet2 = new Meet();
        meet2.setId(2L);
        meet2.setTitle("Meeting 2");
        meet2.setDescription("Description 2");
        meet2.setCreatorId(102L);
        meet2.setStartsAt(LocalDateTime.now());

        List<Long> meetIds = Arrays.asList(1L, 2L);

        when(meetRepository.findAllById(meetIds)).thenReturn(Arrays.asList(meet1, meet2));

        List<Meet> result = meetService.getMeetsByIds(meetIds);

        assertEquals(2, result.size());
        assertEquals("Meeting 1", result.get(0).getTitle());
        assertEquals("Meeting 2", result.get(1).getTitle());

        verify(meetRepository, times(1)).findAllById(meetIds);
    }

    @Test
    public void testGetMeetsByIds_EmptyList() {
        List<Long> meetIds = List.of();

        when(meetRepository.findAllById(meetIds)).thenReturn(List.of());

        List<Meet> result = meetService.getMeetsByIds(meetIds);

        assertEquals(0, result.size());

        verify(meetRepository, times(1)).findAllById(meetIds);
    }

    @Test
    public void testGetMeetsByIds_NoMatches() {
        List<Long> meetIds = Arrays.asList(3L, 4L);

        when(meetRepository.findAllById(meetIds)).thenReturn(List.of());

        List<Meet> result = meetService.getMeetsByIds(meetIds);

        assertEquals(0, result.size());

        verify(meetRepository, times(1)).findAllById(meetIds);
    }
}