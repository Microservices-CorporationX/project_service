package ru.corporationx.projectservice.controller.moment;

import ru.corporationx.projectservice.model.dto.moment.MomentDto;
import ru.corporationx.projectservice.service.moment.MomentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MomentControllerTest {

    @InjectMocks
    private MomentController momentController;

    @Mock
    private MomentService momentService;

    @Test
    void testCreateMoment() {
        MomentDto momentDto = new MomentDto();
        momentService.createMoment(momentDto);
        verify(momentService, times(1)).createMoment(momentDto);
    }

    @Test
    void testUpdateMoment() {
        Long momentId = 1L;
        MomentDto momentDto = new MomentDto();
        momentService.updateMoment(momentDto, momentId);
        verify(momentService, times(1)).updateMoment(momentDto, momentId);
    }

    @Test
    void testGetAllMoments() {
        momentService.getAllMoments();
        verify(momentService, times(1)).getAllMoments();
    }

    @Test
    void testGetMomentById() {
        momentService.getMomentById(1L);
        verify(momentService, times(1)).getMomentById(1L);
    }


}