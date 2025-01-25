package faang.school.projectservice.service;

import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void testGetScheduleById() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setName("Test Schedule");

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        Schedule result = scheduleService.getScheduleById(1L);

        assertNotNull(result);
        assertEquals("Test Schedule", result.getName());
        verify(scheduleRepository, times(1)).findById(1L);
    }

    @Test
    void testGetScheduleById_NotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> scheduleService.getScheduleById(1L));

        verify(scheduleRepository, times(1)).findById(1L);
    }
}