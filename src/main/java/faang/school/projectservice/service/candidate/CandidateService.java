package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;

import java.util.List;

public interface CandidateService {
    void deleteById(Long id);

    List<Candidate> findAllByVacancyId(Long vacancyId);
}
