package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    @Override
    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        candidateRepository.deleteById(id);
    }

    @Override
    public List<Candidate> findAllByVacancyId(Long vacancyId) {
        return candidateRepository.findAllByVacancyId(vacancyId);
    }
}
