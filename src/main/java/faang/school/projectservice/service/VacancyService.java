package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final

    public CreateVacancyResponse create(CreateVacancyRequest createRequest) {

    }

    public UpdateVacancyResponse update(UpdateVacancyRequest updateRequest) {

    }

    public void delete(long id) {

    }

    public GetVacancyResponse get(long id) {

    }

    public List<GetVacancyResponse> getAll() { // фильтрация

    }
}
