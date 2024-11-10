package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.dto.client.internShip.InternshipUpdatedDto;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {
    @InjectMocks
    private InternshipController internshipController;

    @Mock
    private InternshipService internshipService;

    @Test
    public void testExistingInternshipCreating() {
        InternshipCreatedDto internShipCreatedDto = InternshipCreatedDto.builder().build();
        when(internshipService.createInternship(any(InternshipCreatedDto.class))).thenReturn(internShipCreatedDto);
        InternshipCreatedDto result = internshipController.createInternship(internShipCreatedDto);
        assertEquals(internShipCreatedDto, result);
    }

    @Test
    public void testExistingInternshipUpdating() {
        InternshipUpdatedDto internShipUpdatedDto = InternshipUpdatedDto.builder().build();
        when(internshipService.updateInternship(any(InternshipUpdatedDto.class))).thenReturn(internShipUpdatedDto);
        InternshipUpdatedDto result = internshipController.updateInternship(internShipUpdatedDto);
        assertEquals(internShipUpdatedDto, result);
    }

}
