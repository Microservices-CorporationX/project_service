package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectService projectService;

    public VacancyDto create(VacancyDto vacancyDto) {
        return vacancyDto;
    }

    public Vacancy toEntityFromDto(VacancyDto dto) {
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setProject(projectService.getProjectById(dto.getProjectId()));
        return vacancy;
    }
}
