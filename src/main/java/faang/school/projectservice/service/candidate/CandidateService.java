package faang.school.projectservice.service.candidate;

import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public void deleteById(Long id) {
        candidateRepository.deleteById(id);
        log.info("Delete candidate with ID {}",id);
    }
}
