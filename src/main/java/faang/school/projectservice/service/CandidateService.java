package faang.school.projectservice.service;

import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.validator.CandidateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final CandidateValidator candidateValidator;

    public void deleteCandidateById(long candidateId) {
        candidateValidator.validateCandidateExistsById(candidateId);
        candidateRepository.deleteById(candidateId);
        log.info("Candidate with id {} deleted successfully", candidateId);
    }
}
