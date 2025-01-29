package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface VacancyService {

    void addCover(Long vacancyId, MultipartFile file);
}
