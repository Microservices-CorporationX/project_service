package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public void addInternship(InternshipDto internshipDto) {
        validateDescriptionAndName(internshipDto);
        internshipService.addInternship(internshipDto);
    }

    private void validateDescriptionAndName(InternshipDto internshipDto) {
        String description = internshipDto.getDescription();
        String name = internshipDto.getName();
        long id = internshipDto.getId();
        if (description == null || description.isEmpty()) {
            throw new DataValidationException("Description by internship " + id + " is empty");
        }
        if (name == null || name.isEmpty()) {
            throw new DataValidationException("Name by internship " + id + " is empty");
        }
    }


}
