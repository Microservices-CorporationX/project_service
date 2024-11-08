package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;

import java.util.List;

public interface CandidateService {
    Candidate findById(Long id);
    void deleteById(Long id);
    List<Candidate> findAllByVacancyId(Long vacancyId);
}
