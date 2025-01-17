package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.TeamRole;
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

    public List<VacancyDto> filterByPosition(TeamRole position) {
        return service.filterByPosition(position);
    }

    public List<VacancyDto> filterByName(String str) {
        return service.filterByName(str);
    }
}
