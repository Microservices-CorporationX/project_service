package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipStatusDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.InternshipService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping("/internship")
    public InternshipDto create(@RequestBody InternshipDto internshipDto) {
        validateInternshipDtoBasic(internshipDto);
        return internshipService.create(internshipDto);
    }

    @PutMapping("/internship")
    public InternshipDto update(@RequestBody InternshipDto internshipDto) {
        validateInternshipDtoBasic(internshipDto);
        validateInternshipDtoStatus(internshipDto);

        return internshipService.update(internshipDto);
    }

    @PostMapping("/internships/filter")
    public List<InternshipDto> getFilteredInternships(@RequestBody InternshipFilterDto filterDto) {
        validateFilters(filterDto);
        return internshipService.getFilteredInternships(filterDto);
    }

    @GetMapping("/internships")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/internships/{id}")
    public InternshipDto getInternshipById(@PathVariable String id) {
        validateId(id);
        return internshipService.getInternshipById(Long.parseLong(id)).orElse(null);
    }

    private void validateInternshipDtoBasic(InternshipDto internshipDto) {
        if(internshipDto.internIds() == null || internshipDto.internIds().isEmpty()) {
            throw new DataValidationException("List of interns should not be empty");
        }

        if(internshipDto.mentorId() == null || internshipDto.mentorId() <= 0) {
            throw new DataValidationException("Mentor should not be empty");
        }

        if(internshipDto.ownedProjectId() == null || internshipDto.ownedProjectId() <= 0) {
            throw new DataValidationException("Project id should not be empty");
        }
    }

    private void validateInternshipDtoStatus(InternshipDto internshipDto) {
        InternshipStatusDto status = internshipDto.status();

        if(status != InternshipStatusDto.IN_PROGRESS && status != InternshipStatusDto.COMPLETED) {
            throw new DataValidationException("Incorrect status of internship");
        }
    }

    private void validateFilters(InternshipFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataValidationException("Object of filters is null");
        }
    }

    private void validateId(String id) {
        if(id == null) {
            throw new DataValidationException("Internship id is null");
        }

        long idValue = Long.parseLong(id);

        if(idValue <= 0) {
            throw new DataValidationException("Incorrect internship's id");
        }
    }
}
