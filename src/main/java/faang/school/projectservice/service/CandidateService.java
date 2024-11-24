package faang.school.projectservice.service;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public void deleteCandidateByIdByStatus(Long idCandidate, CandidateStatus candidateStatusNotDelete) {
        Candidate candidate = candidateRepository.findById(idCandidate).orElse(null);
        if (candidate == null) {
            log.info("Candidate with id {} did n't find", idCandidate);
        } else {
            if (candidate.getCandidateStatus() == null || candidate.getCandidateStatus() != candidateStatusNotDelete) {
                candidateRepository.deleteById(idCandidate);
                log.info("CandidateService. Candidate with id {} deleted", idCandidate);
            }

        }
    }
}