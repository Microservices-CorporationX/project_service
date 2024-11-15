package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(candidateRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getCandidate()));
        Mockito.lenient().when(candidateRepository.findAllByVacancyId(Mockito.anyLong())).thenReturn(getCandidateList());
    }

    @Test
    void deleteById() {
        candidateService.deleteById(1L);
        Mockito.verify(candidateRepository).deleteById(1L);
    }

    @Test
    void getCandidates() {
        List<Candidate> candidates = candidateRepository.findAllByVacancyId(1L);
        assertEquals(getCandidateList(), candidates);
        Mockito.verify(candidateRepository).findAllByVacancyId(1L);
    }

    private List<Candidate> getCandidateList() {
        return List.of(getCandidate(), getCandidate(), getCandidate());
    }

    private Candidate getCandidate() {
        return new Candidate();
    }
}