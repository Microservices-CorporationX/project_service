package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Internships.", description = "API for managing internships.")
@RequestMapping("/internship")
@RestController
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    public ResponseEntity<InternshipDto> createInternship(InternshipCreationDto internshipCreationDto) {
        InternshipDto internshipDto = internshipService.createInternship(internshipCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(internshipDto);
    }
}
