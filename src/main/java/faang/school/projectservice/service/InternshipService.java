package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void createInternship(InternshipDto dto) {
        validateInternship(dto);
        internshipRepository.save(internshipMapper.toEntity(dto));
    }

    public void updateInternship(InternshipDto dto) {
        validateInternship(dto);
        if (dto.getEndDate().isBefore(LocalDateTime.now())) {
            Internship updateInternship = internshipRepository.getById(dto.getId());
            List<TeamMember> teamInterns = updateInternship.getInterns();
            teamInterns.stream()
                    .forEach(intern -> intern.setRoles(List.of(dto.getRole())));
        } else {
            Internship internship = internshipRepository.findById(dto.getId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Element with id" + dto.getId() + "dose not exist"));
            internshipMapper.update(dto, internship);
            internshipRepository.save(internship);
        }
    }

    public List<InternshipDto> getFilteredInternshipsByStatus(InternshipStatus status) {
        return internshipRepository.findAll().stream()
                .filter(internship -> internship.getStatus().equals(status))
                .map(internship -> internshipMapper.toDto(internship))
                .toList();
    }

    public List<InternshipDto> getFilteredInternshipsByRole(List<TeamRole> filterRoles) {
        return internshipRepository.findAll().stream()
                .filter(internship -> filterRoles.stream()
                        .allMatch(role -> internship.getRole().equals(role)))
                .map(internship -> internshipMapper.toDto(internship))
                .toList();
    }

    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internship -> internshipMapper.toDto(internship))
                .toList();
    }

    public InternshipDto getInternshipById(long internshipId) {
        return internshipMapper.toDto(internshipRepository.findById(internshipId)
                .orElseThrow(() -> new NoSuchElementException("Element with id" + internshipId + "dose not exist")));
    }

    public void removeInternFromInternship(List<Long> internIds, long internshipId) {
        removeOrFinishAheadInternshipData(internIds, internshipId, false);
    }

    public void finishTheInternshipAheadOfScheduleFor(List<Long> internIds, long internshipId) {
        removeOrFinishAheadInternshipData(internIds, internshipId, true);
    }

    private void removeOrFinishAheadInternshipData(List<Long> internIds, long internshipId, boolean finishInternshipAhead) {
        Internship internship = internshipRepository.getById(internshipId);
        if (finishInternshipAhead) {
            internship.getInterns().stream()
                    .forEach(intern -> intern.setRoles(List.of(internship.getRole())));
        }
        List<TeamMember> updatedInternsList = internship.getInterns().stream()
                .filter(intern -> !internIds.contains(intern.getId()))
                .toList();
        internship.setInterns(updatedInternsList);
        internshipRepository.save(internship);
    }

    private void validateInternship(InternshipDto dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        long monthsBetween = ChronoUnit.MONTHS.between(dto.getStartDate().toLocalDate(), dto.getEndDate().toLocalDate());
        if (monthsBetween > 3) {
            throw new IllegalArgumentException("Internship duration cannot exceed 3 months");
        }
        long mentorId = dto.getMentorId();
        Project project = projectRepository.getById(dto.getProjectId());
        boolean isInternshipMentorInProjectTeam = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(mentorId));
        if (!isInternshipMentorInProjectTeam) {
            throw new IllegalArgumentException("Mentor must be from project team");
        }
    }
}
