package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.FilterVacancy;
import faang.school.projectservice.mapper.MapperVacancy;
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
public class VacancyService {
    private final VacancyExtraRepository vacancyExtraRepository;
    private final MapperVacancy mapperVacancy;
    private final ValidationVacancies validationVacancies;
    private final CandidateService candidateService;
    private final List<FilterVacancy> filter;
    public final static List<CandidateStatus> CANDIDATE_STATUS_DELETE = List.of(CandidateStatus.WAITING_RESPONSE, CandidateStatus.REJECTED);

    public VacancyDto saveVacancy(VacancyDto vacancyDto) {
        log.info("saveVacancy, vacancyDto:{} ", vacancyDto);

        validationVacancies.projectExist(vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.createdBy(), "createdBy", vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.updatedBy(), "updatedBy", vacancyDto.projectId());

        return mapperVacancy.vacancyToVacancyDTo(vacancyExtraRepository.getVacancyRepository().save(mapperVacancy.vacancyDToToVacancy(vacancyDto)));
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        log.info("updateVacancy, vacancyDto:{} ", vacancyDto);

        validationVacancies.vacancyExist(vacancyDto.id());
        validationVacancies.projectExist(vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.createdBy(), "createdBy", vacancyDto.projectId());
        validationVacancies.personHasNecessaryRole(vacancyDto.updatedBy(), "updatedBy", vacancyDto.projectId());

        Vacancy tempVacancy = vacancyExtraRepository.findById(vacancyDto.id());
        if (vacancyDto.status() == VacancyStatus.CLOSED) {
            validationVacancies.numberCandidatesForCloser(tempVacancy.getCandidates(), tempVacancy.getCount());
        }

        mapperVacancy.update(vacancyDto, tempVacancy);

        return mapperVacancy.vacancyToVacancyDTo(vacancyExtraRepository.getVacancyRepository().save(tempVacancy));
    }

    public VacancyDto deleteVacancy(Long id) {
        log.info("deleteVacancy, id:{} ", id);
        VacancyDto tempVacancyDto = mapperVacancy.vacancyToVacancyDTo(vacancyExtraRepository.findById(id));
        tempVacancyDto.candidates()
                .forEach(idCandidate -> CANDIDATE_STATUS_DELETE
                        .forEach(candidateStatus -> candidateService.deleteCandidateByIdByStatus(idCandidate, candidateStatus)));
        vacancyExtraRepository.getVacancyRepository().deleteById(id);

        log.info("Project with id {} has been deleted", id);

        return tempVacancyDto;
    }

    public List<VacancyDto> findByFilter(FilterVacancyDto filterVacancyDto) {
        log.info("findAll, filterVacancyDto:{} ", filterVacancyDto);
        return filter.stream()
                .filter(filter -> filter.isAvailable(filterVacancyDto))
                .reduce(vacancyExtraRepository.getVacancyRepository().findAll().stream(),
                        (vacancyStream, filter1) -> filter1.apply(vacancyStream, filterVacancyDto),
                        (vacancyStream, vacancyStream2) -> vacancyStream)
                .map(mapperVacancy::vacancyToVacancyDTo)
                .toList();
    }

    public VacancyDto getVacancyById(Long id) {
        log.info("getVacancyById, id:{} ", id);
        return mapperVacancy.vacancyToVacancyDTo(vacancyExtraRepository.getVacancyRepository()
                .findById(id).orElseThrow(() -> new ValidationException(String.format("There is not vacancy with id %s", id))));
    }
}