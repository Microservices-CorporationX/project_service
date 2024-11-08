package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectService projectService;
    private final VacancyValidator vacancyValidator;
    private final ProjectValidator projectValidator;

    public VacancyDto create(VacancyDto vacancyDto) {
        projectValidator.validateProjectExistsById(vacancyDto.getProjectId());
        vacancyValidator.validateVacancyCreatorRole(vacancyDto);
        Vacancy vacancy = mapToEntity(vacancyDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancyRepository.save(vacancy);
        log.info("New vacancy with id #{} successfully saved", vacancy.getId());
        return vacancyMapper.toDto(vacancy);
    }

    private Vacancy mapToEntity(VacancyDto dto) {
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setProject(projectService.getProjectById(dto.getProjectId()));
        return vacancy;
    }
}
