package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @InjectMocks
    private InternshipService internshipService;

    private InternshipDto internshipDto;
    private Internship internship;

    @BeforeEach
    void setUp() {
        internshipDto = InternshipDto.builder()
                .id(1L)
                .name("Java Internship")
                .description("3-month Java training")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(3))
                .build();

        internship = new Internship();
        internship.setId(1L);
    }

    @Test
    void createInternship_ShouldSucceed() {
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(internshipDto);

        InternshipDto result = internshipService.createInternship(internshipDto);

        assertNotNull(result);
        assertEquals("Java Internship", result.getName());
        verify(internshipRepository).save(internship);
    }

    @Test
    void createInternship_ShouldFail_IfDurationExceeds3Months() {
        internshipDto.setEndDate(internshipDto.getStartDate().plusMonths(4));

        assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));
    }
}