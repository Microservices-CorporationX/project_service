package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public void createInternship(InternshipDto internship) {
        internshipService.createInternship(internship);
    }

    public void updateInternship(InternshipDto internship) {
        internshipService.updateInternship(internship);
    }

    public List<InternshipDto> getInternshipsByFilters() {
        return internshipService.getInternshipsByFilters();
    }

    public List<InternshipDto> getInternships() {
        return internshipService.getInternships();
    }

    public InternshipDto getInternshipById(long internshipId) {
        return internshipService.getInternshipById(internshipId);
    }
}
