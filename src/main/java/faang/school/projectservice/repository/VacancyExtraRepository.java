package faang.school.projectservice.repository;

import faang.school.projectservice.model.Vacancy;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Getter
@Repository
@RequiredArgsConstructor
public class VacancyExtraRepository {

    private final VacancyRepository vacancyRepository;

    public Vacancy findById(Long id) {
        return vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %s", id)));
    }
}