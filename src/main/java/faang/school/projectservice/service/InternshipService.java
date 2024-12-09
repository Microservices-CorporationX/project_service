package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    public InternshipDto createInternship(InternshipDto internshipDto) {
        validateInternship(internshipDto);
        TeamMember mentor = teamMemberService.getTeamMember(internshipDto.mentorId());
        List<TeamMember> interns = teamMemberService.getAllTeamMembersByIds(internshipDto.internIds());

        Internship internship = internshipMapper.toInternship(internshipDto);
        internship.setMentorId(mentor);
        internship.setInterns(interns);

        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public void updateInternship(InternshipDto internshipDto) {
        if (projectService.isProjectComplete(internshipDto.projectId())) {
            teamMemberService.setTeamMembersRoleAndRemoveInternRole(internshipDto.internIds(), internshipDto.role());
        } else {
            Internship internship = getInternshipById(internshipDto.id());
            List<TeamMember> teamMembers = teamMemberService.getAllTeamMembersByIds(internshipDto.internIds());
            internship.getInterns().removeAll(teamMembers);
            internshipRepository.save(internship);
        }
    }

    public void changeInternshipStatus(long internshipId, InternshipStatus status) {
        Internship internship = getInternshipById(internshipId);
        internship.setStatus(status);
        internshipRepository.save(internship);
    }

    @Transactional
    public void completeInternship(long internshipId, long teamMemberId) {
        Internship internship = getInternshipById(internshipId);
        TeamMember teamMember = teamMemberService.getTeamMember(teamMemberId);

        internship.getInterns().remove(teamMember);
        teamMemberService.removeTeamRole(teamMemberService.getTeamMember(internshipId), TeamRole.INTERN);
        internshipRepository.save(internship);
    }

    public void dismissTeamMember(long internshipId, long teamMemberId) {
        Internship internship = getInternshipById(internshipId);
        TeamMember teamMember = teamMemberService.getTeamMember(teamMemberId);

        internship.getInterns().remove(teamMember);
        internshipRepository.save(internship);
    }

    public List<InternshipDto> getFilteredInternships(InternshipFilterDto internshipFilterDto) {
        return getAllInternships().stream()
                .filter(intern -> internshipFilterDto.status() == null || intern.status() == internshipFilterDto.status())
                .filter(intern -> internshipFilterDto.status() == null || intern.role() == internshipFilterDto.role())
                .toList();
    }

    public List<InternshipDto> getAllInternships() {
        List<Internship> internships = internshipRepository.findAll();
        if (internships.isEmpty()) {
            throw new DataValidationException("Не найдено ни одной стажировки");
        }

        return internshipMapper.toInternshipDtos(internships);
    }

    public InternshipDto getInternshipDtoById(long internshipId) {
        return internshipMapper.toInternshipDto(getInternshipById(internshipId));
    }

    private Internship getInternshipById(long internshipId) {
        return internshipRepository.findById(internshipId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id:%d не найден".formatted(internshipId))
        );
    }

    private void validateInternship(InternshipDto internshipDto) {
        if (ChronoUnit.MONTHS.between(internshipDto.startDate(), internshipDto.endDate()) > 3) {
            throw new DataValidationException("Продолжительность стажировки более 3х месяцев");
        }
    }

}
