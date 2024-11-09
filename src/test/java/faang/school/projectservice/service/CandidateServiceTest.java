package faang.school.projectservice.service;

import faang.school.projectservice.repository.CandidateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    CandidateRepository candidateRepository;

    @InjectMocks
    CandidateService candidateService;

    @Test
    @DisplayName("Delete candidate with valid id")
    void testDeleteCandidateIdValid() {
        long candidateId = 1L;

        candidateRepository.deleteById(candidateId);

        verify(candidateRepository, times(1)).deleteById(candidateId);
    }
}