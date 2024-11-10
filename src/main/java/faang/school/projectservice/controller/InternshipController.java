package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internShip.InternShipCreatedDto;
import faang.school.projectservice.dto.client.internShip.InternShipUpdatedDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/create")
    public InternShipCreatedDto create(InternShipCreatedDto internShipCreatedDto) {
        return internshipService.create(internShipCreatedDto);
    }

    @PutMapping("/update")
    public InternShipUpdatedDto update(InternShipUpdatedDto InternShipUpdatedDto) {
        return internshipService.updatedDto(InternShipUpdatedDto);
    }


    public List<InternShipCreatedDto> filter() {
        return null;
    }

    public List<InternShipCreatedDto> getInternships() {
        return null;
    }

    public InternShipCreatedDto getInternShipById(long id) {
        return null;
    }
}
