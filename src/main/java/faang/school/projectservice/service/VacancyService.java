package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectService projectService;
    private final VacancyValidator vacancyValidator;
    private final ProjectValidator projectValidator;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        projectValidator.validateProjectExistsById(vacancyDto.getProjectId());
        vacancyValidator.validateVacancyCreatorRole(vacancyDto);
        Vacancy vacancy = mapToEntity(vacancyDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancyRepository.save(vacancy);
        log.info("New vacancy with id #{} successfully saved", vacancy.getId());
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public VacancyDto updateVacancyStatus(VacancyDto dto) {
        vacancyValidator.validateVacancyCreatorRole(dto);
        Vacancy vacancy = getVacancyById(dto.getId());
        if (dto.getStatus().equals(VacancyStatus.CLOSED)) {
            vacancyValidator.validateCandidateCountForClosure(vacancy);
            vacancy.setStatus(dto.getStatus());
            vacancyRepository.save(vacancy);
        }
        return vacancyMapper.toDto(vacancy);
    }

    public List<Candidate> getCandidatesByVacancyId(Long vacancyId) {
        return getVacancyById(vacancyId).getCandidates();
    }

    public Vacancy getVacancyById(long vacancyId) {
        return vacancyRepository.findById(vacancyId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vacancy not found by id: %s", vacancyId)));
    }

    private Vacancy mapToEntity(VacancyDto dto) {
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setProject(projectService.getProjectById(dto.getProjectId()));
        return vacancy;
    }
}
