package faang.school.projectservice.service;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository repository;

    public void createVacancy(Vacancy vacancy) {
        repository.save(vacancy);
    }

    public void updateVacancy(Vacancy vacancy) {
        repository.save(vacancy);
    }
    public void removeVacancy(Long id) {
        repository.deleteById(id);
    }
}
