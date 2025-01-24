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

    public void validateVacancy(Vacancy vacancy) {
        if (!checkCuratorRole(vacancy)) {
            throw new DataValidationException("Curator is not owner or manager");
        }

        if (!checkCandidatesAreNotTeamMember(vacancy)) {
            throw new DataValidationException("Candidate is team member");
        }
    }

    public void validateClosingVacancy(Vacancy vacancy) {
        if (!checkCountOfCandidates(vacancy)) {
            throw new DataValidationException("There is not enough number of candidates");
        }
    }

    private boolean checkCuratorRole(Vacancy vacancy) {
        TeamMember curator = teamMemberRepository.findByUserIdAndProjectId(vacancy.getCreatedBy(), vacancy.getProject().getId());

        AtomicBoolean IsOwnerOrManager = new AtomicBoolean(false);

        curator.getRoles().forEach(r -> {
            if (r == TeamRole.OWNER || r == TeamRole.MANAGER)
                IsOwnerOrManager.set(true);
        });

        return IsOwnerOrManager.get();
    }

    private boolean checkCandidatesAreNotTeamMember(Vacancy vacancy) {
        for (Candidate candidate : vacancy.getCandidates()) {
            if (teamMemberRepository.findByUserIdAndProjectId(candidate.getUserId(), vacancy.getProject().getId()) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCountOfCandidates(Vacancy vacancy) {
        return vacancy.getCandidates().size() >= vacancy.getCount();
    }
}
