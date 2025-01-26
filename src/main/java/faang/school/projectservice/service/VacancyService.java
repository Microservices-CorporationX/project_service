package faang.school.projectservice.service;

import faang.school.projectservice.exseption.ProjectNotFoundException;
import faang.school.projectservice.exseption.VacancyNotFoundException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VacancyService {

    private static final Logger log = LoggerFactory.getLogger(VacancyService.class);
    private final VacancyRepository vacancyRepository;
    private final VacancyValidatorService validatorService ;
    private final CandidateRepository candidateRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Vacancy createVacancy(Vacancy vacancy, Long currentUserIds, Long projectId) {
        validatorService.validateCuratorHasOwnerOrManagerRole(currentUserIds);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        vacancy.setProject(project);
        vacancyRepository.save(vacancy);
        return vacancy;
    }

    @Transactional
    public Vacancy updateVacancy(Vacancy vacancy, Long vacancyId, Long currentUserIds) {
        Vacancy vacancyToUpdate = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy Not Found"));

        validatorService.validateCuratorHasOwnerOrManagerRole(currentUserIds);
        validatorService.validateVacancyCanBeClosed(vacancyToUpdate, vacancy.getStatus());

        if (vacancy.getPosition() != null) {
            vacancyToUpdate.setPosition(vacancy.getPosition());
        }
        if (vacancy.getCount() != null) {
            vacancyToUpdate.setCount(vacancy.getCount());
        }
        if (vacancy.getStatus() != null) {
            vacancyToUpdate.setStatus(vacancy.getStatus());
        }

        vacancyRepository.save(vacancyToUpdate);
        return vacancyToUpdate;
    }

    @Transactional
    public void addCandidatesToVacancy(List<Long> candidateIds, Long projectId, Long vacancyId, Long currentUserIds) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy Not Found"));

        validatorService.validateCuratorHasOwnerOrManagerRole(currentUserIds);
        if (!candidateIds.isEmpty()) {
            validatorService.ensureCandidatesAreNotProjectMembers(candidateIds, projectId);
        }

        Set<Long> existingCandidateIds = vacancy.getCandidates().stream()
                .map(Candidate::getId)
                .collect(Collectors.toSet());
        List<Long> newCandidateIds = candidateIds.stream()
                .filter(candidateId -> !existingCandidateIds.contains(candidateId))
                .toList();
        if (!newCandidateIds.isEmpty()) {
            List<Candidate> newCandidates = candidateRepository.findAllById(newCandidateIds);
            vacancy.getCandidates().addAll(newCandidates);
        }

        vacancyRepository.save(vacancy);
    }

    @Transactional
    public void removeVacancy(Long vacancyId, Long currentUserIds) {
        validatorService.validateCuratorHasOwnerOrManagerRole(currentUserIds);

        if (!vacancyRepository.existsById(vacancyId)) {
            throw new VacancyNotFoundException("Vacancy Not Found");
        }
        vacancyRepository.deleteById(vacancyId);
        log.info("Vacancy with ID {} successfully deleted by user {}", vacancyId, currentUserIds);
    }

    @Transactional
    public List<Vacancy> filterVacancies(TeamRole position, String name) {
        List<Vacancy> allVacancies = vacancyRepository.findAll();
        if (allVacancies.isEmpty()) {
            throw new VacancyNotFoundException("Vacancy Not Found");
        }
        return allVacancies.stream()
                .filter(vacancy -> Objects.equals(vacancy.getName(), name) && Objects.equals(vacancy.getPosition(),
                        position))
                .toList();
    }

    @Transactional
    public Vacancy getVacancyById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy Not Found"));
    }
}
