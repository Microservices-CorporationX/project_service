package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipRepository internshipRepository;

    public boolean validateForExistInternship(Long internshipId) {
        if (internshipRepository.existsById(internshipId)) {
            log.error("The internship already exists by ID: {}", internshipId);
            throw new DataValidationException("The internship already exists");
        }
        return true;
    }

    public boolean validate(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null) {
            log.error("The internship project is null");
            throw new DataValidationException("The internship must be related to some project");
        }
        if (internshipDto.getInternIds().isEmpty()) {
            log.error("The internship has no interns");
            throw new DataValidationException("An internship is not created without interns");
        }
        if (internshipDto.getMentorId() == null) {
            log.error("The internship has no mentor");
            throw new DataValidationException("An internship won't happen without a mentor");
        }
        return true;
    }

    public boolean validateInternshipTotalDuration(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        if (monthsBetween >= 3) {
            log.error("The internship lasts 3 months or more");
            throw new DataValidationException("The internship cannot last 3 months or more");
        }
        return true;
    }

    public boolean validateInternsNotAddedAfterStart(Internship internship, List<TeamMember> internsBeforeUpdate, InternshipDto dto) {
        boolean isDateChanges = false;
        if (!dto.getStartDate().equals(internship.getStartDate()) || !dto.getEndDate().equals(internship.getEndDate())) {
            isDateChanges = true;
        }
        if (!isDateChanges && !internship.getInterns().equals(internsBeforeUpdate) &&
                internship.getStartDate().isBefore(LocalDateTime.now())) {
            log.error("Trying to attach interns to an internship that has already started");
            throw new DataValidationException("It is not possible to add interns to an internship that has already started");
        }
        return isDateChanges;
    }
}
