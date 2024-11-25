package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VacancyDto(
        @NotNull(groups = After.class)
        Long id,
        @NotNull
        Long projectId,
        @NotNull
        String name,
        @NotNull
        String description,
        @NotNull
        VacancyStatus status,
        @NotNull
        TeamMemberDto supervisor,
        Integer numberOfCandidates
) {
        public interface Before {}
        public interface After {}
}
