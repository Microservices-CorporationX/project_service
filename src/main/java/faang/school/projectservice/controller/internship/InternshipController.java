package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipReadDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipReadDto createInternship(InternshipCreateDto internship) {
        return internshipService.createInternship(internship);
    }

    public InternshipReadDto updateInternship(InternshipEditDto internship) {
        return internshipService.updateInternship(internship);
    }

    public List<InternshipReadDto> getInternshipsByFilters(InternshipFilterDto internship) {
        return internshipService.getInternshipsByFilters(internship);
    }

    public List<InternshipReadDto> getInternships() {
        return internshipService.getInternships();
    }

    public InternshipReadDto getInternshipById(long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}
