package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internShip.InternShipCreatedDto;
import faang.school.projectservice.dto.client.internShip.InternShipUpdatedDto;
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
        InternShipCreatedDto internShipCreatedDto = InternShipCreatedDto.builder().build();
        when(internshipService.create(any(InternShipCreatedDto.class))).thenReturn(internShipCreatedDto);
        InternShipCreatedDto result = internshipController.create(internShipCreatedDto);
        assertEquals(internShipCreatedDto, result);
    }

    @Test
    public void testExistingInternshipUpdating() {
        InternShipUpdatedDto internShipUpdatedDto = InternShipUpdatedDto.builder().build();
        when(internshipService.updatedDto(any(InternShipUpdatedDto.class))).thenReturn(internShipUpdatedDto);
        InternShipUpdatedDto result = internshipController.update(internShipUpdatedDto);
        assertEquals(internShipUpdatedDto, result);
    }

}
