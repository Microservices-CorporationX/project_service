package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public CreateVacancyResponse createVacancy(CreateVacancyRequest createRequest) {

    }
}
