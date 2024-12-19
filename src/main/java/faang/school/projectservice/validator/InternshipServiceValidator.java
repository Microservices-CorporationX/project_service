package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternshipServiceValidator {

    private final Duration maxInternshipDuration = Duration.ofDays(91);
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    public void validateInternshipDuration(InternshipDto internshipDto) {
        if (Duration.between(internshipDto.startDate(), internshipDto.endDate()).toDays() > maxInternshipDuration.toDays()) {
            throw new DataValidationException("Duration of internship more than " + maxInternshipDuration.toDays() / 30 + " month");
        }
    }

    public void validateMentor(InternshipDto internshipDto) {
        List<Long> projectTeamMembersIds = projectService.getAllTeamMembersIds(internshipDto.ownedProjectId());
        boolean isMentorExistInProject = projectTeamMembersIds.stream()
                .anyMatch(id -> id.equals(internshipDto.mentorId()));

        if (!isMentorExistInProject) {
            throw new DataValidationException("Mentor is absent in internship's project");
        }
    }

    public void validateMembersRoles(InternshipDto internshipDto) {
        internshipDto.internIds().forEach(internId -> {
            boolean isIntern = teamMemberService.findById(internId).getRoles().contains(TeamRole.INTERN);

            if (!isIntern) {
                throw new DataValidationException(String.format("User with id %d is not intern", internId));
            }
        });
    }

    public void validateCountOfInterns(InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(internshipDto.id()).orElseThrow(
                () -> new DataValidationException("Internship with id " + internshipDto.id() + " not exists"));

        InternshipDto internshipFromDB = internshipMapper.toDto(internship);

        if (LocalDateTime.now().isAfter(internshipDto.startDate()) &&
                // насколько корректна такая конструкция?
                !new HashSet<>(internshipFromDB.internIds()).containsAll(internshipDto.internIds())) {
            throw new DataValidationException("It is forbidden to add participants after the start of the internship");
        }
    }
}
