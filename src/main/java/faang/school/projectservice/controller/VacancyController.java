package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService service;

    public void createVacancy(VacancyDto vacancy) {
        service.createVacancy(vacancy);
    }

    public void updateVacancy(VacancyDto vacancy) {
        service.updateVacancy(vacancy);
    }

    public void removeVacancy(Long id) {
        service.removeVacancy(id);
    }

    public List<Vacancy> filterByPosition(TeamRole role) {
        return service.filterByPosition(role);
    }

    public List<Vacancy> filterByName(String str) {
        return service.filterByName(str);
    }
}
