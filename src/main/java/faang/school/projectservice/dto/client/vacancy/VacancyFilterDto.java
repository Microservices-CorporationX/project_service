package faang.school.projectservice.dto.client.vacancy;

import lombok.Builder;

@Builder
public record VacancyFilterDto(
        String name,
        String description
) {
}
