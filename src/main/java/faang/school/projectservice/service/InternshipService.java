package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;

    public void createInternship(InternshipDto internshipDto) {
        if (ChronoUnit.MONTHS.between(internshipDto.startDate(), internshipDto.endDate()) > 3) {
            throw new DataValidationException("Продолжительность стажировки более 3х месяцев");
        }
        internshipRepository.save(internshipMapper.toEntity(internshipDto));
    }

    public void updateInternship(InternshipDto internshipDto) {
        if (projectService.isProjectComplete(internshipDto.projectId())) {
            teamMemberService.setTeamMembersRoleNonIntern(internshipDto.internIds());
        } else {
            internshipDto.internIds().clear();
        }
    }

    public void completeInternship(long internshipId, long teamMemberId) {
        removeTeamMembersFromInternship(internshipId, teamMemberId);
        teamMemberService.removeTeamRole(teamMemberService.getTeamMemberById(internshipId), TeamRole.INTERN);
    }

    public void dismissTeamMember(long internshipId, long teamMemberId) {
        removeTeamMembersFromInternship(internshipId, teamMemberId);
    }

    public List<InternshipDto> getFilteredInternships(InternshipStatus internshipStatus) {
        return getAllInternships().stream()
                .filter(internship -> internship.status() == internshipStatus)
                .toList();
    }

    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toDtos(internshipRepository.findAll());
    }

    public InternshipDto getInternshipDtoById(long internshipId) {
        return internshipMapper.toDto(getInternshipById(internshipId));
    }

    private Internship getInternshipById(long internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) {
            throw new EntityNotFoundException("Пользователь с id:%d не найден".formatted(internshipId));
        }

        return internship;
    }

    private void removeTeamMembersFromInternship(long internshipId, long teamMemberId) {
        Internship internship = getInternshipById(internshipId);
        TeamMember teamMember = teamMemberService.getTeamMemberById(teamMemberId);

        internship.getInterns().remove(teamMember);
    }

}
