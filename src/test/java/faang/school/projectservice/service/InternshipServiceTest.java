package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internship.InternshipCreateRequest;
import faang.school.projectservice.dto.client.internship.InternshipUpdateRequest;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validation.InternshipValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private InternshipValidationService internshipValidationService;
    @Mock
    private Internship internship;

    @Spy
    private InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);

    @InjectMocks
    private InternshipService internshipService;


    @Test
    public void createInternshipSuccessfully() {
        InternshipCreateRequest dto = new InternshipCreateRequest(1L, 1L, 1L,
                List.of(1L),
                LocalDateTime.of(2025, 1, 29, 13, 00),
                LocalDateTime.of(2025, 2, 28, 13, 00),
                "Some description",
                "some name",
                TeamRole.DEVELOPER);

        Internship actualInternship = internshipMapper.toEntity(dto);
        internshipService.createInternship(dto);

        verify(internshipValidationService, times(1)).validateRequest(dto);
        verify(internshipMapper, times(2)).toEntity(dto);
        verify(internshipRepository, times(1)).save(actualInternship);

    }

    @Test
    public void createInternshipWithIncorrectData() {
        InternshipCreateRequest dto = new InternshipCreateRequest(1L, 1L, 1L,
                List.of(1L),
                LocalDateTime.of(2025, 1, 29, 13, 00),
                LocalDateTime.of(2025, 2, 28, 13, 00),
                "Some description",
                "some name",
                TeamRole.DEVELOPER);

        internshipService.createInternship(dto);
        doThrow(new IllegalArgumentException()).when(internshipValidationService).validateRequest(dto);

        verify(internshipValidationService, times(1)).validateRequest(dto);
        assertThrows(IllegalArgumentException.class, () -> internshipValidationService.validateRequest(dto));
    }

    @Test
    public void updateInternship_ShouldValidateAndUpdateRoles_WhenEndDatePassed() {
        InternshipUpdateRequest dto = mock(InternshipUpdateRequest.class);
        when(dto.getId()).thenReturn(1L);
        when(dto.getEndDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(dto.getRole()).thenReturn(TeamRole.DEVELOPER);

        Internship internship = mock(Internship.class);
        TeamMember firstIntern = mock(TeamMember.class);
        TeamMember secondIntern = mock(TeamMember.class);

        List<TeamMember> interns = List.of(firstIntern, secondIntern);

        when(internshipRepository.getById(dto.getId())).thenReturn(internship);
        when(internship.getInterns()).thenReturn(interns);

        internshipService.updateInternship(dto);

        verify(internshipValidationService, times(1)).validateRequest(dto);
        verify(internshipRepository, times(1)).save(internship);

        for (TeamMember intern : interns) {
            verify(intern).setRoles(argThat(roles ->
                    roles.contains(TeamRole.DEVELOPER) && !roles.contains(TeamRole.INTERN)
            ));
        }
    }

    @Test
    public void updateInternshipWithNewData() {
        InternshipUpdateRequest dto = new InternshipUpdateRequest(
                1L, 2L, 3L, List.of(1L,2L,3L),
                LocalDateTime.of(2025,5,1,23,00),
                LocalDateTime.of(2025,6,1,23,00),
                "some description",
                "some name",
                TeamRole.DEVELOPER
        );

    }
}
