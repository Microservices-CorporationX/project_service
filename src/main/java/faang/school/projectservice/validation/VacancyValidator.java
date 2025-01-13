package faang.school.projectservice.validation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final TeamMemberRepository memberRepository;

    public void createValidate(Vacancy vacancy) {
        creatorValidate(vacancy.getCreatedBy());
        if (vacancy.getCount() < 1) {
            throw new IllegalArgumentException("Количество вакантных мест не может быть меньше 1");
        }
    }

    public void updateValidate(Vacancy vacancy) {
        creatorValidate(vacancy.getCreatedBy());
        if (vacancy.getCandidates().size() != vacancy.getCount()) {
        }
    }

    private void creatorValidate(Long creatorId) {
        Optional<TeamMember> creator = memberRepository.findById(creatorId);
        if (creator.isEmpty()) {
            throw new IllegalArgumentException("Не удалось получить автора вакансии по id");
        }
        if (creator.get().getRoles().stream().noneMatch(
                role -> role.equals(TeamRole.MANAGER) || role.equals(TeamRole.OWNER))) {
            throw new IllegalArgumentException("Автор вакансии не является OWNER'ом и MANAGER'ом");
        }
    }
}
