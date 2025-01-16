package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        validateInternshipDates(internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);
        Internship savedInternship = internshipRepository.save(internship);

        return internshipMapper.toDto(savedInternship);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        Internship existingInternship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));

        if (existingInternship.getStatus() == InternshipStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a completed internship.");
        }

        existingInternship.setStatus(internshipDto.getStatus());
        Internship updatedInternship = internshipRepository.save(existingInternship);

        return internshipMapper.toDto(updatedInternship);
    }

    public InternshipDto getInternshipById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));

        return internshipMapper.toDto(internship);
    }

    public List<InternshipDto> getInternships(InternshipStatus status, Long roleId) {
        return internshipRepository.findAll().stream()
                .filter(i -> status == null || i.getStatus() == status)
                .map(internshipMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateInternshipDates(InternshipDto internshipDto) {
        Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (duration.toDays() > 90) {
            throw new IllegalArgumentException("Internship cannot last longer than 3 months.");
        }
    }
}