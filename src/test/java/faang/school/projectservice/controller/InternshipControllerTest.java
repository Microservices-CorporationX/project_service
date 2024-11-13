package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipGetAllDto;
import faang.school.projectservice.dto.internship.InternshipGetByIdDto;
import faang.school.projectservice.dto.internship.InternshipUpdatedDto;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

    @Test
    public void testFilterInternship() {
        InternshipFilterDto filterDto = new InternshipFilterDto();
        InternshipFilterDto resultDto = new InternshipFilterDto();
        List<InternshipFilterDto> expected = List.of(resultDto);

        when(internshipService.filterInternship(filterDto)).thenReturn(expected);

        List<InternshipFilterDto> result = internshipService.filterInternship(filterDto);

        verify(internshipService).filterInternship(filterDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetAllInternship() {
        InternshipGetAllDto getAllDto = new InternshipGetAllDto();
        List<InternshipGetAllDto> expected = List.of(getAllDto);

        when(internshipService.getAllInternships()).thenReturn(expected);

        List<InternshipGetAllDto> result = internshipService.getAllInternships();

        verify(internshipService).getAllInternships();

        assertEquals(expected, result);
    }

    @Test
    public void testGetInternshipById() {
        long internshipId = 1L;
        InternshipGetByIdDto getByIdDto = InternshipGetByIdDto.builder().build();

        when(internshipService.getByIdInternship(internshipId)).thenReturn(getByIdDto);

        InternshipGetByIdDto result = internshipService.getByIdInternship(internshipId);

        verify(internshipService).getByIdInternship(internshipId);

        assertEquals(getByIdDto, result);
    }
}
