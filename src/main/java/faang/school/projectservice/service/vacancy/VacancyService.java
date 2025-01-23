package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
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
    private final CandidateService candidateService;

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

    public Vacancy updateVacancy(Long vacancyId, Vacancy newVacancy, Long tutorId) {
        if (vacancyId == null || newVacancy == null || tutorId == null) {
            throw new DataValidationException("vacancyId, newVacancy or tutorId is null");
        }
        Vacancy sourceVacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateTutorRole(tutorId, newVacancy.getProject().getId());
        vacancyValidator.validateVacancyStatus(sourceVacancy);
        Vacancy targetVacancy = Vacancy.builder()
                .id(vacancyId)
                .createdAt(sourceVacancy.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .createdBy(sourceVacancy.getCreatedBy())
                .updatedBy(tutorId)
                .build();

        if (newVacancy.getName() != null) {
            targetVacancy.setName(newVacancy.getName());
        }
        if (newVacancy.getDescription() != null) {
            targetVacancy.setDescription(newVacancy.getDescription());
        }
        if (newVacancy.getPosition() != null) {
            targetVacancy.setPosition(newVacancy.getPosition());
        }
        if (newVacancy.getProject() != null) {
            targetVacancy.setProject(newVacancy.getProject());
        }
        if (!newVacancy.getCandidates().isEmpty()) {
            targetVacancy.setCandidates(newVacancy.getCandidates());
        }
        if (newVacancy.getStatus() != null) {
            targetVacancy.setStatus(newVacancy.getStatus());
        }
        if (newVacancy.getSalary() != null) {
            targetVacancy.setSalary(newVacancy.getSalary());
        }
        if (newVacancy.getWorkSchedule() != null) {
            targetVacancy.setWorkSchedule(newVacancy.getWorkSchedule());
        }
        if (newVacancy.getCount() != null) {
            targetVacancy.setCount(newVacancy.getCount());
        }
        if (!newVacancy.getRequiredSkillIds().isEmpty()) {
            targetVacancy.setRequiredSkillIds(newVacancy.getRequiredSkillIds());
        }
        if (newVacancy.getCoverImageKey() != null) {
            targetVacancy.setCoverImageKey(newVacancy.getCoverImageKey());
        }

        return vacancyRepository.save(targetVacancy);
    }

    public void deleteVacancy(Long vacancyId, Long tutorId) {
        if (vacancyId == null || tutorId == null) {
            throw new DataValidationException("vacancyId or tutorId is null");
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() ->
                new DataValidationException("vacancy %d not found".formatted(vacancyId)));

        vacancyValidator.validateTutorRole(tutorId, vacancy.getProject().getId());
        candidateService.deleteCandidatesByVacancyId(vacancyId);
        vacancyRepository.deleteById(vacancyId);
    }
}
