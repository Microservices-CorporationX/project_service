package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internShip.InternshipCreatedDto;
import faang.school.projectservice.dto.internShip.InternshipFilterDto;
import faang.school.projectservice.dto.internShip.InternshipGetAllDto;
import faang.school.projectservice.dto.internShip.InternshipGetByIdDto;
import faang.school.projectservice.dto.internShip.InternshipUpdatedDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipCreatedDto createInternship(InternshipCreatedDto internShipCreatedDto) {
        return internshipService.createInternship(internShipCreatedDto);
    }

    public InternshipUpdatedDto updateInternship(InternshipUpdatedDto InternShipUpdatedDto) {
        return internshipService.updateInternship(InternShipUpdatedDto);
    }


    public List<InternshipFilterDto> getAllInternshipsByFilter(InternshipFilterDto internShipFilterDto) {
        return internshipService.filterInternship(internShipFilterDto);
    }

    public List<InternshipGetAllDto> getAllInternship() {
        return internshipService.getAllInternships();
    }

    public InternshipGetByIdDto getInternshipById(long internshipId) {
        return internshipService.getByIdInternship(internshipId);
    }
}
