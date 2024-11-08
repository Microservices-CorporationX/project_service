package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void findByIdEmpty() {
        Mockito.when(candidateRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> candidateService.findById(1L));
        assertEquals("Candidate id %s not found".formatted(1L), exception.getMessage());
    }

    @Test
    void findByIdSuccess() {
        assertEquals(getCandidate(), candidateService.findById(Mockito.anyLong()));
    }

    @Test
    void deleteById() {
        candidateService.deleteById(1L);
        Mockito.verify(candidateRepository).deleteById(1L);
    }

    @Test
    void getCandidates() {
        assertEquals(getCandidateList(), candidateRepository.findAllByVacancyId(Mockito.anyLong()));
    }

    private List<Candidate> getCandidateList() {
        return List.of(getCandidate(), getCandidate(), getCandidate());
    }

    private Candidate getCandidate() {
        return new Candidate();
    }
}