package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
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
        MomentDto momentDto = new MomentDto();
        momentService.updateMoment(momentDto);
        verify(momentService, times(1)).updateMoment(momentDto);
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