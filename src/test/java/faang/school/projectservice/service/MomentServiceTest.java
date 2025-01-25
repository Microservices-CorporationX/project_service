package faang.school.projectservice.service;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
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
public class MomentServiceTest {

    @Mock
    private MomentRepository momentRepository;

    @InjectMocks
    private MomentService momentService;

    @Test
    public void testGetMomentsByIds_ValidIds() {
        Moment moment1 = new Moment();
        moment1.setId(1L);
        moment1.setName("Moment 1");
        moment1.setDescription("Description 1");
        moment1.setDate(LocalDateTime.now());

        Moment moment2 = new Moment();
        moment2.setId(2L);
        moment2.setName("Moment 2");
        moment2.setDescription("Description 2");
        moment2.setDate(LocalDateTime.now());

        List<Long> momentIds = Arrays.asList(1L, 2L);

        when(momentRepository.findAllById(momentIds)).thenReturn(Arrays.asList(moment1, moment2));

        List<Moment> result = momentService.getMomentsByIds(momentIds);

        assertEquals(2, result.size());
        assertEquals("Moment 1", result.get(0).getName());
        assertEquals("Moment 2", result.get(1).getName());

        verify(momentRepository, times(1)).findAllById(momentIds);
    }

    @Test
    public void testGetMomentsByIds_EmptyList() {
        List<Long> momentIds = List.of();

        when(momentRepository.findAllById(momentIds)).thenReturn(List.of());

        List<Moment> result = momentService.getMomentsByIds(momentIds);

        assertEquals(0, result.size());

        verify(momentRepository, times(1)).findAllById(momentIds);
    }

    @Test
    public void testGetMomentsByIds_NoMatches() {
        List<Long> momentIds = Arrays.asList(3L, 4L);

        when(momentRepository.findAllById(momentIds)).thenReturn(List.of());

        List<Moment> result = momentService.getMomentsByIds(momentIds);

        assertEquals(0, result.size());

        verify(momentRepository, times(1)).findAllById(momentIds);
    }
}