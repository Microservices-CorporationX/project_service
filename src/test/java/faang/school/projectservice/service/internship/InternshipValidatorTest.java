package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {
    @InjectMocks
    private InternshipValidator internshipValidator;

    @Mock
    private InternshipRepository internshipRepository;

    @Test
    void testValidateInternshipDoesNotExist() {
        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .build();
        when(internshipRepository.existsById(internshipDto.getId())).thenReturn(false);

        assertTrue(internshipValidator.validateForExistInternship(internshipDto.getId()));
    }

    @Test
    void testValidateInternshipAlreadyExist() {
        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .build();
        when(internshipRepository.existsById(internshipDto.getId())).thenReturn(true);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipValidator.validateForExistInternship(internshipDto.getId()));

        assertEquals("The internship already exists", exception.getMessage());
    }

    @Test
    void testValidateNullProjectId() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipValidator.validate(InternshipDto.builder().build()));

        assertEquals("The internship must be related to some project", exception.getMessage());
    }

    @Test
    void testValidateIsInternsListEmpty() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipValidator.validate(InternshipDto.builder()
                        .projectId(1L)
                        .internIds(List.of())
                        .build()));

        assertEquals("An internship is not created without interns", exception.getMessage());
    }

    @Test
    void testValidateNullMentorId() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipValidator.validate(InternshipDto.builder()
                        .projectId(1L)
                        .internIds(List.of(2L))
                        .build()));

        assertEquals("An internship won't happen without a mentor", exception.getMessage());
    }

    @Test
    void testValidateMentorIdNotNull() {
        assertTrue(internshipValidator.validate(InternshipDto.builder()
                        .projectId(1L)
                        .internIds(List.of(2L))
                        .mentorId(1L)
                        .build()));
    }

    @Test
    void testValidateIfInternshipLastsThreeMonthsOrMore() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.of(2024, 6, 1, 10, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 10, 0))
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipValidator.validateInternshipTotalDuration(internshipDto));

        assertEquals("The internship cannot last 3 months or more", exception.getMessage());
    }

    @Test
    void testValidateIfInternshipDoesNotLastThreeMonthsOrMore() {
        InternshipDto internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.of(2024, 6, 1, 10, 0))
                .endDate(LocalDateTime.of(2024, 8, 1, 10, 0))
                .build();

        assertTrue(internshipValidator.validateInternshipTotalDuration(internshipDto));
    }

    @Test
    void testValidateInternshipUpdateIfAddedNewInterns() {
        InternshipDto dto = prepareData();
        List<TeamMember> internsBeforeUpdate = List.of(new TeamMember(), new TeamMember());
        Internship internshipAfterUpdate = Internship.builder()
                .interns(List.of(new TeamMember(), new TeamMember(), new TeamMember(), new TeamMember()))
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipValidator.validateInternsNotAddedAfterStart(internshipAfterUpdate, internsBeforeUpdate, dto));

        assertEquals("It is not possible to add interns to an internship that has already started", exception.getMessage());
    }

    @Test
    void testValidateInternshipUpdateIfNoAddedNewInterns() {
        InternshipDto dto = prepareData();
        TeamMember firstMember = new TeamMember();
        TeamMember secondMember = new TeamMember();
        List<TeamMember> internsBeforeUpdate = List.of(firstMember, secondMember);
        Internship internshipAfterUpdate = Internship.builder()
                .interns(List.of(firstMember, secondMember))
                .startDate(LocalDateTime.of(2024, 10, 1, 10, 0))
                .build();

        assertTrue(internshipValidator.validateInternsNotAddedAfterStart(internshipAfterUpdate, internsBeforeUpdate, dto));
    }

    private InternshipDto prepareData() {
        return InternshipDto.builder()
                .startDate(LocalDateTime.of(2024, 10, 1, 10, 0))
                .endDate(LocalDateTime.of(2025, 01, 1, 10, 0))
                .build();
    }
}
