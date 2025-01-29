package faang.school.projectservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService{

    @Override
    public void addCover(Long vacancyId, MultipartFile file) {

    }
}
