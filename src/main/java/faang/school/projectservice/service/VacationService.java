package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.MapperVacation;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyExtraRepository;
import faang.school.projectservice.validation.ValidationVacancies;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {
    private final VacancyExtraRepository vacancyExtraRepository;
    private final MapperVacation mapperVacation;
    private final ValidationVacancies validationVacancies;
    private final CandidateService candidateService;
    private final List<VacancyFilter> filter;
    private final List<CandidateStatus> CANDIDATE_STATUS_DELETE = List.of(CandidateStatus.WAITING_RESPONSE, CandidateStatus.REJECTED);

    public VacancyDto saveVacation(VacancyDto vacancyDto) {
        log.info("saveVacation, vacancyDto:{} ", vacancyDto);

        validationVacancies.isProjectExist(vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.createdBy(), "createdBy", vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.updatedBy(), "updatedBy", vacancyDto.projectId());

        return mapperVacation.vacancyToVacancyDTo(vacancyExtraRepository.getVacancyRepository().save(mapperVacation.vacancyDToToVacancy(vacancyDto)));
    }

    public VacancyDto updateVacation(VacancyDto vacancyDto) {
        log.info("updateVacation, vacancyDto:{} ", vacancyDto);

        validationVacancies.isVacancyExist(vacancyDto.id());
        validationVacancies.isProjectExist(vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.createdBy(),"createdBy", vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.updatedBy(), "updatedBy", vacancyDto.projectId());

        Vacancy tempVacancy = vacancyExtraRepository.findById(vacancyDto.id());
        if (vacancyDto.status() == VacancyStatus.CLOSED) {
            validationVacancies.numberCandidatesForCloser(tempVacancy.getCandidates(), tempVacancy.getCount());
        }

        mapperVacation.update(vacancyDto, tempVacancy);

        return mapperVacation.vacancyToVacancyDTo(vacancyExtraRepository.getVacancyRepository().save(tempVacancy));
    }

    public VacancyDto deleteVacation(Long id) {
        log.info("deleteVacation, id:{} ", id);
        VacancyDto tempVacancyDto = mapperVacation.vacancyToVacancyDTo(vacancyExtraRepository.findById(id));
        tempVacancyDto.candidates()
                .forEach(idCandidate -> CANDIDATE_STATUS_DELETE
                        .forEach(candidateStatus -> candidateService.deleteCandidateByIdByStatus(idCandidate, candidateStatus)));
        vacancyExtraRepository.getVacancyRepository().deleteById(id);

        log.info("Project with id {} has been deleted", id);

        return tempVacancyDto;
    }

    public List<VacancyDto> findAll(FilterVacancyDto filterVacancyDto) {
        log.info("findAll, filterVacancyDto:{} ", filterVacancyDto);
        return filter.stream()
                .filter(filter -> filter.isAvailable(filterVacancyDto))
                .reduce(vacancyExtraRepository.getVacancyRepository().findAll().stream(),
                        (vacancyStream, filter1) -> filter1.apply(vacancyStream, filterVacancyDto),
                        (vacancyStream, vacancyStream2) -> vacancyStream)
                .map(mapperVacation::vacancyToVacancyDTo)
                .toList();
    }

    public VacancyDto getVacancyById(Long id) {
        log.info("getVacancyById, id:{} ", id);
        return mapperVacation.vacancyToVacancyDTo(vacancyExtraRepository.getVacancyRepository()
                .findById(id).orElseThrow(() -> new ValidationException(String.format("There is not vacancy with id %s", id))));
    }
}