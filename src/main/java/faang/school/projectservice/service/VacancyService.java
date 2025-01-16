package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository repository;
    private final VacancyValidator validator;
    private final VacancyMapper mapper;

    public void createVacancy(VacancyDto dto) {
        Vacancy vacancy = mapper.toEntity(dto);
        validator.createValidate(vacancy);
        repository.save(vacancy);
    }

    public void updateVacancy(VacancyDto dto) {
        Vacancy vacancy = mapper.toEntity(dto);
        validator.updateValidate(vacancy);
        repository.save(vacancy);
    }

    public void removeVacancy(Long id) {
        validator.removeValidate(id);
        repository.deleteById(id);
    }

    public List<Vacancy> filterByPosition(TeamRole role) {
        return repository.findAll().stream().filter(vacancy -> vacancy.getPosition() == role).toList();
    }

    public List<Vacancy> filterByName(String str) {
        return repository.findAll().stream().filter(vacancy -> vacancy.getName().contains(str)).toList();
    }
}
