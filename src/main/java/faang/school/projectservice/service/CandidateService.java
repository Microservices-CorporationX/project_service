package faang.school.projectservice.service;

import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public void deleteCandidateById(long candidateId) {
        candidateRepository.deleteById(candidateId);
    }
}
