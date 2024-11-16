package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipGetAllDto;
import faang.school.projectservice.dto.internship.InternshipGetByIdDto;
import faang.school.projectservice.dto.internship.InternshipUpdatedDto;
import faang.school.projectservice.service.InternshipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/internship")
    public InternshipCreatedDto createInternship(@Valid @RequestBody InternshipCreatedDto internship) {
        log.info("Creating internship '{}' by UserId #{}.", internship.getName(), internship.getCreatedBy());
        return internshipService.createInternship(internship);
    }

    @PutMapping("/internship")
    public InternshipUpdatedDto updateInternship(@Valid @RequestBody InternshipUpdatedDto internship) {
        log.info("Updating internship '{}' by UserId: {}.", internship.getId(), internship.getCreatedBy());
        return internshipService.updateInternship(internship);
    }

    @GetMapping("/internship/filter")
    public List<InternshipFilterDto> getAllInternshipsByFilter(@RequestParam InternshipFilterDto filterInternship) {
        log.info("Filtering all internships by User {}.", filterInternship.getCreatedBy());
        return internshipService.filterInternship(filterInternship);
    }

    @GetMapping("/internship")
    public List<InternshipGetAllDto> getAllInternship() {
        log.info("Getting all internships.");
        List<InternshipGetAllDto> result = internshipService.getAllInternships();
        log.info("Found {} internships", result.size());
        return result;
    }

    @GetMapping("/internship/{internshipId}")
    public InternshipGetByIdDto getInternshipById(@PathVariable @Positive long internshipId) {
        log.info("Getting internship by Id {}", internshipId);
        return internshipService.getByIdInternship(internshipId);
    }
}
