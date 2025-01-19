package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public void createInternship(InternshipCreateDto internship) {
        internshipService.createInternship(internship);
    }

    public void updateInternship(InternshipCreateDto internship) {
        internshipService.updateInternship(internship);
    }

    public List<InternshipCreateDto> getInternshipsByFilters() {
        return internshipService.getInternshipsByFilters();
    }

    public List<InternshipCreateDto> getInternships() {
        return internshipService.getInternships();
    }

    public InternshipCreateDto getInternshipById(long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}
