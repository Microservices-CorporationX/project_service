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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VacancyService {

    private static final Logger log = LoggerFactory.getLogger(VacancyService.class);
    private final VacancyRepository vacancyRepository;
    private final ValidateService validateService;
    private final CandidateRepository candidateRepository;
    private final ProjectRepository projectRepository;

    public void createVacancy(Vacancy vacancyDto, Long currentUserIds, Long projectId) {
        validateService.validateCuratorRole(currentUserIds);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        vacancyDto.setProject(project);
        vacancyRepository.save(vacancyDto);
    }

    public void updateVacancy(Vacancy vacancyDto, Long vacancyId, Long currentUserIds) {
        if (vacancyDto.getPosition() == null || vacancyDto.getStatus() == null || vacancyDto.getCount() == null) {
            throw new IllegalArgumentException("Position and Status cannot be null");
        }
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy Not Found"));
        validateService.validateCuratorRole(currentUserIds);
        validateService.validateVacancyClosure(vacancy, vacancyDto.getStatus());

        vacancy.setPosition(vacancyDto.getPosition());
        vacancy.setCount(vacancyDto.getCount());
        vacancy.setStatus(vacancyDto.getStatus());

        vacancyRepository.save(vacancy);
    }

    public void addCandidatesToVacancy(List<Long> candidateIds, Long projectId, Long vacancyId, Long currentUserIds) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy Not Found"));
        validateService.validateCuratorRole(currentUserIds);
        if (!candidateIds.isEmpty()) {
            validateService.validateCandidatesNotInProject(candidateIds, projectId);
        }

        if (vacancy.getCandidates().isEmpty()) {
            List<Candidate> candidate = candidateRepository.findAllById(candidateIds);
            vacancy.getCandidates().addAll(candidate);
        } else {
            Set<Long> existingById = vacancy.getCandidates().stream()
                    .map(Candidate::getId)
                    .collect(Collectors.toSet());
            List<Long> candidateToAdd = candidateIds.stream()
                    .filter((candidateId) -> !existingById.contains(candidateId))
                    .toList();
            if (!candidateToAdd.isEmpty()) {
                List<Candidate> candidate = candidateRepository.findAllById(candidateToAdd);
                vacancy.getCandidates().addAll(candidate);
            }
        }
        vacancyRepository.save(vacancy);
    }

    public void removeVacancy(Long vacancyId, Long currentUserIds) {
        validateService.validateCuratorRole(currentUserIds);

        if (!vacancyRepository.existsById(vacancyId)) {
            throw new VacancyNotFoundException("Vacancy Not Found");
        }
        vacancyRepository.deleteById(vacancyId);
        log.info("Vacancy with ID {} successfully deleted by user {}", vacancyId, currentUserIds);
    }

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

    public Vacancy getVacancyById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy Not Found"));
    }
}
