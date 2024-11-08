package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VacancyServiceValidator {

    private final TeamService teamService;

    public void validateCreateVacancy(VacancyDto vacancyDto) {
        Optional<TeamMember> teamMember = teamService.findMemberByUserIdAndProjectId(vacancyDto.getCreatedBy(), vacancyDto.getProjectId());

        if (teamMember.isPresent()) {
            if (!teamMember.get().getRoles().contains(TeamRole.OWNER) || teamMember.get().getRoles().contains(TeamRole.MANAGER)) {
                throw new IllegalArgumentException("Team member id %s dont have needed role".formatted(vacancyDto.getCreatedBy()));
            }
        } else {
            throw new IllegalArgumentException("Team member id %s not found".formatted(vacancyDto.getCreatedBy()));
        }
    }

    public void validateCloseVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getCount() > vacancyDto.getCandidateIds().size()) {
            throw new IllegalArgumentException("Vacancy id %s count is greater than candidate count".formatted(vacancyDto.getId()));
        }
    }
}
