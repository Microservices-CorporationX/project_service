package faang.school.projectservice.controller.stage_invitation;

import faang.school.projectservice.controller.MomentController;
import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Validated
@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    @Mock
    private MomentService momentService;

    @InjectMocks
    private MomentController momentController;

    private MomentDto momentDto;
    private List<MomentDto> momentDtoList;
    private MomentFilterDto momentFilterDto;

    @BeforeEach
    public void setup() {
        momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setName("Sample Moment");
        momentDtoList = List.of(momentDto);
    }

    @Test
    public void testSaveMoment() {
        when(momentService.saveMoment(any(MomentDto.class))).thenReturn(momentDto);

        ResponseEntity<MomentDto> response = momentController.saveMoment(momentDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(momentDto, response.getBody());
        verify(momentService, times(1)).saveMoment(momentDto);
    }

    @Test
    public void testUpdateMomentWithParthner() {
        when(momentService.updateMoment(any(MomentDto.class))).thenReturn(momentDto);

        ResponseEntity<MomentDto> response = momentController.updateMomentWithParthner(momentDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(momentDto, response.getBody());
        verify(momentService, times(1)).updateMoment(momentDto);
    }

    @Test
    public void testGetMomentsWithFilterReturnsEmptyList() {
        momentFilterDto = MomentFilterDto.builder()
                .month(Month.MARCH)
                .projectIds(List.of(1L, 3L))
                .build();
        when(momentService.getMoments(momentFilterDto)).thenReturn(List.of());

        ResponseEntity<List<MomentDto>> response = momentController.getMomentsWithFilter(momentFilterDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().isEmpty());
        verify(momentService, times(1)).getMoments(momentFilterDto);
    }

    @Test
    public void testGetMomentsWithFilterReturnsList() {
        momentFilterDto = MomentFilterDto.builder()
                .month(Month.MARCH)
                .projectIds(List.of(1L, 3L))
                .build();
        when(momentService.getMoments(momentFilterDto)).thenReturn(momentDtoList);

        ResponseEntity<List<MomentDto>> response = momentController.getMomentsWithFilter(momentFilterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(momentDtoList, response.getBody());
        verify(momentService, times(1)).getMoments(momentFilterDto);
    }

    @Test
    public void testGetAllMomentsReturnsEmptyList() {
        when(momentService.getAllMoments()).thenReturn(List.of());

        ResponseEntity<List<MomentDto>> response = momentController.getAllMoments();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().isEmpty());
        verify(momentService, times(1)).getAllMoments();
    }

    @Test
    public void testGetAllMomentsReturnsList() {
        when(momentService.getAllMoments()).thenReturn(momentDtoList);

        ResponseEntity<List<MomentDto>> response = momentController.getAllMoments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(momentDtoList, response.getBody());
        verify(momentService, times(1)).getAllMoments();
    }

    @Test
    public void testGetMoment() {
        long momentId = 1L;
        when(momentService.getMoment(momentId)).thenReturn(momentDto);

        ResponseEntity<MomentDto> response = momentController.getMoment(momentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(momentDto, response.getBody());
        verify(momentService, times(1)).getMoment(momentId);
    }
}
