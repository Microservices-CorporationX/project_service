package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;

    @Transactional
    public Internship createInternship(Internship internship) {
        return internshipRepository.save(internship);
    }

    @Transactional
    public Internship updateInternship(Internship internship) {
        Internship existingInternship = internshipRepository.findById(internship.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));

        if (existingInternship.getStatus() == InternshipStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a completed internship.");
        }

        existingInternship.setStatus(internship.getStatus());
        return internshipRepository.save(existingInternship);
    }

    @Transactional
    public Internship partialUpdateInternship(Long id, InternshipDto internshipDto) {
        Internship existingInternship = internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));

        if (internshipDto.getName() != null) {
            existingInternship.setName(internshipDto.getName());
        }
        if (internshipDto.getDescription() != null) {
            existingInternship.setDescription(internshipDto.getDescription());
        }
        if (internshipDto.getStartDate() != null) {
            existingInternship.setStartDate(internshipDto.getStartDate());
        }
        if (internshipDto.getEndDate() != null) {
            existingInternship.setEndDate(internshipDto.getEndDate());
        }
        if (internshipDto.getStatus() != null) {
            existingInternship.setStatus(internshipDto.getStatus());
        }

        return internshipRepository.save(existingInternship);
    }

    @Transactional(readOnly = true)
    public Internship getInternshipById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));
    }

    @Transactional(readOnly = true)
    public List<Internship> getInternships(InternshipStatus status, Long roleId) {
        return internshipRepository.findAll().stream()
                .filter(i -> status == null || i.getStatus() == status)
                .collect(Collectors.toList());
    }
}