package faang.school.projectservice.validator;

import faang.school.projectservice.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandidateValidator {
    private CandidateRepository candidateRepository;

    public void validateCandidateExistsById(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new EntityNotFoundException(String.format("Candidate with id %d doesn't exist", candidateId));
        }
    }
}
