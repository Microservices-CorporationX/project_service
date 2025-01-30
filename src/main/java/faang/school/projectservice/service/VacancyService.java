package faang.school.projectservice.service;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository repository;
    private final VacancyMapper mapper;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getPositionId() == null
                || vacancyDto.getProjectId() == null
                || vacancyDto.getCuratorRoleId() == null) {
            throw new NullPointerException("You are use illegal data: position and project must be not null");
        }
        if (vacancyDto.getCuratorRoleId() != 0
                && vacancyDto.getCuratorRoleId() != 1) {
            throw new IllegalArgumentException("You are use illegal data: curator must be OWNER or MANAGER");
        }
        Vacancy vacancy = mapper.toEntity(vacancyDto);
        return mapper.toDto(repository.save(vacancy));
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getStatusId() == 1) {
            if (vacancyDto.getCandidatesIds() == null
                    || vacancyDto.getCandidatesIds().size() < vacancyDto.getCount()) {
                throw new IllegalStateException(
                        "Vacancy cannot be closed: not enough candidates selected."
                );
            }
        }
        Vacancy vacancy = getVacancyById(vacancyDto);
        List<Candidate> candidate = vacancy.getCandidates();
        if (candidate.stream().allMatch(candidate1 -> candidate1.getCandidateStatus() != null))
            vacancy = mapper.update(vacancy, vacancyDto);
        return mapper.toDto(repository.save(vacancy));}


    public void deleteVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = getVacancyById(vacancyDto);
        List<Candidate> candidate = vacancy.getCandidates();
        if (candidate != null && !candidate.isEmpty()) {
            candidate.forEach(candidate1 -> candidate1.setVacancy(null));
        }
        else {throw new IllegalArgumentException("Vacancy cannot be deleted: there are candidates in the vacancy");}
        repository.deleteById(vacancy.getId());
    }

    private Vacancy getVacancyById(VacancyDto vacancyDto) {
        return repository.findById(vacancyDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vacancy not found"));
    }

    public List<Vacancy> filterVacancies(String position, String name) {
        return repository.findAll()
                .stream()
                .filter(vacancy -> vacancy
                        .getPosition()
                        .name()
                        .equals(position)
                        && vacancy.getName().equals(name))
                .collect(Collectors.toList());
    }

}
