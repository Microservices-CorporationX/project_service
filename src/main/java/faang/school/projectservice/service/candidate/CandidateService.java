package faang.school.projectservice.service.candidate;

import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public List<Long> getAllCandidatesByVacancy(Long vacancyId){
        if (vacancyId == null){
            log.error("Vacancy id is null");
            throw new VacancyValidationException("Vacancy id is null");
        }
        return candidateRepository.findAllByVacancyId(vacancyId).stream()
                .map(Candidate::getUserId).toList();
    }

    public void deleteCandidates(List<Long> userIds){
        candidateRepository.deleteByUserIdIn(userIds);
    }
}
