package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final CandidateService candidateService;
    private final TeamMemberService teamMemberService;
    private final List<VacancyFilter> vacancyFilters;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);

        validateCurator(vacancy);

        Vacancy saveVacancy = vacancyRepository.save(vacancy);
        log.info("Create vacancy with ID {}", saveVacancy.getId());
        return vacancyMapper.toDto(saveVacancy);
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);

        VacancyStatus status = vacancy.getStatus();
        if (status.equals(VacancyStatus.CLOSED) && vacancy.getCount() > vacancy.getCandidates().size()) {
            throw new DataValidationException("The required number of candidates was not recruited");
        }

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        log.info("Update vacancy with ID {}", vacancyId);
        return vacancyMapper.toDto(updatedVacancy);
    }

    public void deleteVacancy(Long id) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        if (vacancy.isEmpty()) {
            throw new DataValidationException("No vacancy with this ID");
        }

        List<Candidate> candidateIds = vacancy.get().getCandidates();
        for (Candidate candidate : candidateIds) {
            candidateService.deleteById(candidate.getId());
        }

        log.info("Delete vacancy with ID {}", id);
        vacancyRepository.deleteById(id);
    }

    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyDto findById(Long id) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        if (vacancy.isEmpty()) {
            throw new DataValidationException("No vacancy with this id");
        }

        log.info("Find vacancy with ID {}", id);
        return vacancyMapper.toDto(vacancy.get());
    }

    private void validateCurator(Vacancy vacancy) {
        if(vacancy.getCreatedBy() == null) {
            throw new DataValidationException("Curator null");
        }
        Long curatorId = vacancy.getCreatedBy();
        TeamMember curator = teamMemberService.findById(curatorId);
        if (!curator.getRoles().contains(TeamRole.MANAGER)) {
            throw new DataValidationException("Curator not have the manager role");
        }
    }
}