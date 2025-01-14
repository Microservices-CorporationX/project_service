package faang.school.projectservice.service;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository repository;
    private final VacancyValidator validator;

    public void createVacancy(Vacancy vacancy) {
        validator.createValidate(vacancy);
        repository.save(vacancy);
    }

    public void updateVacancy(Vacancy vacancy) {
        validator.updateValidate(vacancy);
        repository.save(vacancy);
    }
    public void removeVacancy(Long id) {
        validator.removeValidate(id);
        repository.deleteById(id);
    }
}
