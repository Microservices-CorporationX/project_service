package faang.school.projectservice.service;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CandidateService candidateService;
    private final TeamMemberService teamMemberService;
    private final VacancyMapper vacancyMapper;

    public void createVacancy(VacancyDto vacancyDto) {
        long curatorId = vacancyDto.createdBy();
        checkCuratorAccess(curatorId);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy with ID {} created successfully", vacancy.getId());
    }

    public void updateVacancy(VacancyDto vacancyDto) {
        long vacancyId = vacancyDto.id();
        long curatorId = vacancyDto.createdBy();
        checkCuratorAccess(curatorId);

        Vacancy vacancy = findVacancy(vacancyId);

        if (vacancyDto.name() != null) {
            vacancy.setName(vacancyDto.name());
        }
        if (vacancyDto.description() != null) {
            vacancy.setDescription(vacancyDto.description());
        }
        if (vacancyDto.candidatesIds() != null) {
            List<Candidate> candidates = candidateService.findCandidates(vacancyDto.candidatesIds());
            for (Candidate candidate : candidates) {
                candidate.setVacancy(vacancy);
                vacancy.addCandidate(candidate);
            }
        }
        if (vacancyDto.status() != null) {
            vacancy.setStatus(vacancyDto.status());
        }
        if (vacancyDto.count() != null) {
            vacancy.setCount(vacancyDto.count());
        }

        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancyRepository.save(vacancy);
        log.info("Vacancy with ID {} updated successfully", vacancyId);
    }

    public void deleteVacancy(VacancyDto vacancyDto) {
        long vacancyId = vacancyDto.id();
        Vacancy vacancy = findVacancy(vacancyId);
        List<Candidate> candidates = vacancy.getCandidates();
        for (Candidate candidate : candidates) {
            candidateService.deleteCandidate(candidate.getId());
        }
        vacancyRepository.deleteById(vacancyId);
        log.info("Vacancy with ID {} deleted successfully", vacancyId);
    }

    public List<VacancyDto> getFilteredVacancies(String name, String position) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        List<Vacancy> filteredVacancies = vacancies.stream()
                .filter(vacancy -> vacancy.getName().contains(name) && vacancy.getDescription().contains(position))
                .toList();
        return vacancyMapper.toDto(filteredVacancies);
    }

    public VacancyDto getVacancy(long vacancyId) {
        Vacancy vacancy = findVacancy(vacancyId);
        return vacancyMapper.toDto(vacancy);
    }

    private Vacancy findVacancy(long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new DataValidationException("Vacancy not found"));
    }

    private void checkCuratorAccess(long curatorId) {
        if (!teamMemberService.hasCuratorAccess(curatorId)) {
            log.error("Curator with ID {} does not have access to create a vacancy", curatorId);
            throw new DataValidationException("Curator does not have access to create a vacancy");
        }
    }
}
