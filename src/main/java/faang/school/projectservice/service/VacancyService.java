package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VacancyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VacancyService.class);
    private final VacancyValidator vacancyValidator = new VacancyValidator();

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public void createVacancy(VacancyDto vacancyDto) {
        long curatorId = vacancyDto.createdBy();
        if (!hasCuratorAccess(curatorId)) {
            LOGGER.error("Curator with ID {} does not have access to create a vacancy", curatorId);
            throw new DataValidationException("Curator does not have access to create a vacancy");
        }

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        LOGGER.info("Vacancy with ID {} created successfully", vacancy.getId());
    }

    public void updateVacancy(long vacancyId, Candidate candidate) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        vacancyValidator.validateVacancyIfFound(vacancy, vacancyId);
        vacancy.get().getCandidates().add(candidate);
        vacancyRepository.save(vacancy.get());
        LOGGER.info("Vacancy with ID {} updated successfully", vacancyId);
    }

    public void deleteVacancy(long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        vacancyValidator.validateVacancyIfFound(vacancy, vacancyId);
        vacancyRepository.delete(vacancy.get());
        LOGGER.info("Vacancy with ID {} deleted successfully", vacancyId);
    }

    public List<VacancyDto> getFilteredVacancies(String name, String position) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        return vacancies.stream()
                .filter(vacancy -> vacancy.getName().contains(name) && vacancy.getDescription().contains(position))
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyDto getVacancy(long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        vacancyValidator.validateVacancyIfFound(vacancy, vacancyId);
        LOGGER.info("Vacancy with ID {} found", vacancyId);
        return vacancyMapper.toDto(vacancy.get());
    }

    private boolean hasCuratorAccess(Long curatorId) {
        TeamMember teamMember = teamMemberRepository.findById(curatorId);
        return teamMember.getRoles().contains(TeamRole.MANAGER);
    }
}
