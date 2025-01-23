package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectService projectService;
    private final VacancyValidator vacancyValidator;

    public Vacancy createVacancy(Vacancy vacancy, Long userId) {
        Project project = projectService.getProjectById(vacancy.getProject().getId());
        vacancy.setCreatedBy(userId);
        vacancy.setProject(project);
        vacancy.setCandidates(List.of());
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setUpdatedAt(null);
        vacancy.setUpdatedBy(null);
        vacancy.setStatus(VacancyStatus.OPEN);

        vacancyValidator.validateTutorRole(vacancy.getCreatedBy(), vacancy.getProject().getId());

        log.info("Vacancy created: " + vacancy);
        return vacancyRepository.save(vacancy);
    }

    public Vacancy closeVacancy(Long vacancyId, Long tutorId) {
        if (vacancyId == null || tutorId == null) {
            throw new DataValidationException("vacancyId or tutorId is null");
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateVacancyStatus(vacancy);
        vacancyValidator.validateTutorRole(vacancy.getCreatedBy(), vacancy.getProject().getId());
        vacancyValidator.validateCandidatesCount(vacancy);

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancy.setUpdatedBy(tutorId);
        log.info("Vacancy close: " + vacancy);
        return vacancyRepository.save(vacancy);
    }
}
