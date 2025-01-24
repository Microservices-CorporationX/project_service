package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;

    private final VacancyMapper vacancyMapper;

    private final VacancyValidator vacancyValidator;

    public CreateVacancyResponse create(CreateVacancyRequest createRequest) {
        Vacancy vacancy = vacancyMapper.fromCreateRequest(createRequest);
        Project project = projectRepository.getReferenceById(createRequest.getProjectId());
        vacancy.setProject(project);

        vacancyValidator.validateVacancy(vacancy);

        Vacancy newVacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toCreateResponse(newVacancy);
    }

    public UpdateVacancyResponse update(UpdateVacancyRequest updateRequest) {

    }

    public void delete(long id) {

    }

    public GetVacancyResponse getById(long id) {

    }

    public List<GetVacancyResponse> getAll() { // с фильтрацией

    }
}
