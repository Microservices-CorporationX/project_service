package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternshipValidatorTest {

    @Spy
    private InternshipValidator internshipValidator;
    private Internship internship;
    private InternshipCreateDto internshipCreateDto;
    private InternshipUpdateDto internshipUpdateDto;
    private Project project;
    private Team team ;
    private TeamMember mentor;
    private TeamMember intern;
    private List<TeamMember> interns;
    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate = LocalDateTime.now().plusDays(MAX_DURATION);
    private static final long MAX_DURATION = 92;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .tasks(List.of())
                .build();
        team = Team.builder().project(project).build();
        mentor = TeamMember.builder()
                .team(team)
                .id(1L)
                .build();
        intern = TeamMember.builder()
                .id(2L)
                .team(team)
                .build();
        interns = List.of(intern);
        startDate = LocalDateTime.now();
        endDate = LocalDateTime.now().plusDays(MAX_DURATION);
        internshipCreateDto = InternshipCreateDto.builder()
                .projectId(project.getId())
                .mentorId(mentor.getId())
                .internsId(List.of(intern.getId()))
                .status(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .role(TeamRole.DEVELOPER)
                .build();

        internshipUpdateDto = InternshipUpdateDto.builder()
                .internsId(List.of(intern.getId()))
                .status(InternshipStatus.COMPLETED)
                .build();

        internship = new Internship();
        internship.setId(1L);
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setInterns(interns);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setStartDate(LocalDateTime.now());
        internship.setStartDate(startDate);
        internship.setEndDate(endDate);
        internship.setRole(TeamRole.DEVELOPER);
    }

   @Test
    public void InternshipCreateValidatorWrongMentorTest() {
        Project wrongProject = new Project();
        mentor = TeamMember.builder()
                .team(Team.builder()
                        .project(wrongProject)
                        .build())
                .build();
        internship.setMentor(mentor);
        BusinessException ex = assertThrows(BusinessException.class, () -> internshipValidator.internshipCreateValidate(internship));
        assertEquals(ex.getMessage(), "Ментор с id:" + mentor.getId() + " не участвует в проекте!!!");
   }

    @Test
    public void InternshipCreateValidatorWrongInternTest() {
        Project wrongProject = new Project();
        intern = TeamMember.builder()
                .team(Team.builder()
                        .project(wrongProject)
                        .build())
                .build();
        internship.setInterns(List.of(intern));
        BusinessException ex = assertThrows(BusinessException.class, () -> internshipValidator.internshipCreateValidate(internship));
        assertEquals(ex.getMessage(), "Стажер с id:" + intern.getId() + " не участвует в проекте!!!");
    }

    @Test
    public void completedInternshipUpdateValidatorTest() {
        internship.setStatus(InternshipStatus.COMPLETED);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> internshipValidator.internshipUpdateValidation(internship, internshipUpdateDto));
        assertEquals(ex.getMessage(), "Стажирока окончена!!!");
    }

    @Test
    public void InvalidInternshipUpdateValidatorTest() {
        System.out.println(internshipValidator.isInternsListNotEqualNotEmpty(internship, internshipUpdateDto));
        internshipValidator.internshipUpdateValidation(internship, internshipUpdateDto);
    }

}
