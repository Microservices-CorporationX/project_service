package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateNewVacancy(Vacancy vacancy) {
        if (!checkRoleOfCreatedBy(vacancy)) {
            throw new DataValidationException("Curator is not owner or manager");
        }

        if (!checkCandidatesAreNotTeamMember(vacancy)) {
            throw new DataValidationException("Candidate is team member");
        }
    }

    public void validateUpdatedVacancy(Vacancy vacancy) {
        // проверить что пользователь из updated_by, имеет роль owner или manager

        // если статус вакансии хотят изменить на CLOSED, надо проверить:
        // - количество кандидатов на вакансию равно заявленной квоте
    }

    public void validateClosingVacancy(Vacancy vacancy) {
        if (!checkCountOfCandidates(vacancy)) {
            throw new DataValidationException("There is not enough number of candidates");
        }
    }

    private boolean checkRoleOfCreatedBy(Vacancy vacancy) {
        TeamMember curator = teamMemberRepository.findByUserIdAndProjectId(
                vacancy.getCreatedBy(),
                vacancy.getProject().getId());

        AtomicBoolean isOwnerOrManager = new AtomicBoolean(false);
        curator.getRoles().forEach(r -> {
            if (r == TeamRole.OWNER || r == TeamRole.MANAGER) {
                isOwnerOrManager.set(true);
            }
        });
        return isOwnerOrManager.get();
    }

    private boolean checkRoleOfUpdatedBy(Vacancy vacancy) {
        TeamMember curator = teamMemberRepository.findByUserIdAndProjectId(
                vacancy.getUpdatedBy(),
                vacancy.getProject().getId());

        AtomicBoolean isOwnerOrManager = new AtomicBoolean(false);
        curator.getRoles().forEach(r -> {
            if (r == TeamRole.OWNER || r == TeamRole.MANAGER) {
                isOwnerOrManager.set(true);
            }
        });
        return isOwnerOrManager.get();
    }

    private boolean checkCandidatesAreNotTeamMember(Vacancy vacancy) {
        for (Candidate candidate : vacancy.getCandidates()) {
            TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(
                    candidate.getUserId(),
                    vacancy.getProject().getId()
            );

            if (teamMember != null) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCountOfCandidates(Vacancy vacancy) {
        return vacancy.getCandidates().size() >= vacancy.getCount();
    }
}
