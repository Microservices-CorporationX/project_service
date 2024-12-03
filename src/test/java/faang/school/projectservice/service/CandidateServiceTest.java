package faang.school.projectservice.service;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {
    @Mock
    private CandidateRepository candidateRepository;
    @InjectMocks
    private CandidateService candidateService;


    @Test
    void deleteCandidateByIdByStatusSuccess() {

        Long idCandidate = 1L;
        Candidate candidate = new Candidate();
        when(candidateRepository.findById(idCandidate)).thenReturn(Optional.of(candidate));

        VacationService.CANDIDATE_STATUS_DELETE.forEach(candidateStatus -> {
            candidate.setCandidateStatus(candidateStatus);
            candidateService.deleteCandidateByIdByStatus(idCandidate,
                    candidateStatus);
        });
        verify(candidateRepository,
                times(VacationService.CANDIDATE_STATUS_DELETE.size()))
                .deleteById(idCandidate);
    }

    @Test
    void deleteCandidateByIdNotFoundSuccess() {
        Long idCandidate = 2L;
        when(candidateRepository.findById(idCandidate)).thenReturn(Optional.empty());
        candidateService.deleteCandidateByIdByStatus(idCandidate,
                VacationService.CANDIDATE_STATUS_DELETE.get(0));
        verify(candidateRepository, times(0)).deleteById(idCandidate);
    }

    @Test
    void deleteCandidateByIdStatusNollSuccess() {
        Long idCandidate = 2L;
        Candidate candidate = new Candidate();
        when(candidateRepository.findById(idCandidate)).thenReturn(Optional.of(candidate));
        candidateService.deleteCandidateByIdByStatus(idCandidate,
                VacationService.CANDIDATE_STATUS_DELETE.get(0));
        verify(candidateRepository, times(0)).deleteById(idCandidate);
    }


}
