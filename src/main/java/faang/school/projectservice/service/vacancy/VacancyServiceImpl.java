package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.candidate.CandidateService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validator.vacancy.VacancyServiceValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyServiceValidator vacancyServiceValidator;
    private final VacancyMapper vacancyMapper;

    private final List<VacancyFilter> vacancyFilters;

    private final ProjectService projectService;
    private final CandidateService candidateService;
    private final TeamService teamService;

    @Override
    public VacancyDto createVacancy(@Valid VacancyDto vacancyDto) {
        vacancyServiceValidator.validateCreateVacancy(vacancyDto);

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setId(null);
        vacancy.setProject(projectService.getProjectEntityById(vacancyDto.getProjectId()));
        vacancy.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        vacancy.setStatus(VacancyStatus.OPEN);

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Override
    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository
                .findById(vacancyDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vacancy id %s not found".formatted(vacancyDto.getId())));

        if (vacancyDto.getStatus().equals(VacancyStatus.CLOSED)) {
            return closeVacancy(vacancy);
        }

        vacancy.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        vacancy.setUpdatedBy(vacancyDto.getUpdatedBy());
        vacancy.setStatus(vacancyDto.getStatus());
        vacancy.setProject(projectService.getProjectEntityById(vacancyDto.getProjectId()));

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    private VacancyDto closeVacancy(Vacancy vacancy) {
        vacancyServiceValidator.validateCloseVacancy(vacancyMapper.toDto(vacancy));

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .forEach(candidate -> {
                    TeamMember teamMember = teamService.findMemberByUserIdAndProjectId(candidate.getUserId(), vacancy.getProject().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Member not found by project id %s and user id %s".formatted(vacancy.getProject().getId(), candidate.getUserId())));
                    teamMember.getRoles().add(TeamRole.DEVELOPER);
                });

        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Override
    public void deleteVacancy(Long vacancyId) {
        List<Candidate> candidates = candidateService.findAllByVacancyId(vacancyId).stream()
                .filter(candidate -> candidate.getCandidateStatus().equals(CandidateStatus.WAITING_RESPONSE) || candidate.getCandidateStatus().equals(CandidateStatus.REJECTED))
                .toList();

        candidates.forEach(candidate -> candidateService.deleteById(candidate.getId()));
        candidates.forEach(candidate -> teamService.deleteMemberByUserId(candidate.getUserId()));

        vacancyRepository.deleteById(vacancyId);
    }

    @Override
    public List<VacancyDto> getVacancies(VacancyFilterDto filters) {
        if (filters == null) {
            return vacancyMapper.toDto(vacancyRepository.findAll());
        }

        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(vacancies.stream(),
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .map(vacancyMapper::toDto)
                .toList();
    }

    @Override
    public VacancyDto getVacancy(Long id) {
        return vacancyMapper.toDto(
                vacancyRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Vacancy id %s not found".formatted(id)))
        );
    }
}