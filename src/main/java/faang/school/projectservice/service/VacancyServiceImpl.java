package faang.school.projectservice.service;

import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService{
    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;
    private final S3Service s3Service;

    @Override
    public void addCover(Long vacancyId, MultipartFile file) {
        Vacancy vacancy = vacancyRepositoryAdapter.findById(vacancyId);
//        vacancy.setCoverImageKey();
        String folder = "vacancy" + vacancyId;
        s3Service.uploadFile(file,folder);

    }
}
