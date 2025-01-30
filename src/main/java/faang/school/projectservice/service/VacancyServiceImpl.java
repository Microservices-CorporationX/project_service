package faang.school.projectservice.service;

import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService{
    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;

    @Override
    public void addCover(Long vacancyId, MultipartFile file) {
        Vacancy vacancy = vacancyRepositoryAdapter.findById(vacancyId);
        vacancy.setCoverImageKey();


    }
}
