package faang.school.projectservice.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipStatusDto;
import faang.school.projectservice.dto.internship.RoleDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternshipServiceValidatorTest {

    private static final Long INTERNSHIP_DTO_ID = 222L;
    private static final Long MENTOR_ID = 1L;
    private static final List<Long> INTERN_IDS = List.of(2L, 3L, 4L, 5L);
    private static final String INTERNSHIP_DTO_NAME = "dtoName";
    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, 4, 1, 0, 0, 0);
    private static final LocalDateTime LATEST_END_DATE = LocalDateTime.of(2024, 7, 7, 0, 0, 0);
    private static final Long PROJECT_ID = 111L;
    private final TeamMember mentor = TeamMember.builder().id(1L).userId(21L).roles(List.of(TeamRole.DEVELOPER))
            .build();
    private final List<TeamMember> mentorList = List.of(mentor);


    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private InternshipRepository internshipRepository;
    @Spy
    private InternshipMapperImpl internshipMapper;
    @InjectMocks
    private InternshipServiceValidator validator;

    @Test
    public void testInternshipDuration() {
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, MENTOR_ID, PROJECT_ID,
                InternshipStatusDto.COMPLETED, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, LATEST_END_DATE);

        assertThrows(DataValidationException.class, () -> validator.validateInternshipDuration(internshipDto));
    }

    @Test
    public void testValidateMentor() {
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, 235L, PROJECT_ID,
                InternshipStatusDto.COMPLETED, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, LATEST_END_DATE);
        when(projectService.getAllTeamMembersIds(internshipDto.ownedProjectId())).thenReturn(List.of(1L, 2L));

        assertThrows(DataValidationException.class, () -> validator.validateMentor(internshipDto));
    }

    @Test
    public void testValidateMembersRoles() {
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, 235L, PROJECT_ID,
                InternshipStatusDto.COMPLETED, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, LATEST_END_DATE);
        when(teamMemberService.findById(anyLong())).thenReturn(mentor);

        assertThrows(DataValidationException.class, () -> validator.validateMembersRoles(internshipDto));
    }

    @Test
    public void testValidateCountOfInternsInternshipAbsent() {
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, 235L, PROJECT_ID,
                InternshipStatusDto.COMPLETED, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, LATEST_END_DATE);

        assertThrows(DataValidationException.class, () -> validator.validateCountOfInterns(internshipDto));
    }

    @Test
    public void testValidateCountOfInterns() {
        InternshipDto internshipDto = new InternshipDto(INTERNSHIP_DTO_ID, INTERNSHIP_DTO_NAME, 235L, PROJECT_ID,
                InternshipStatusDto.COMPLETED, RoleDto.DEVELOPER, INTERN_IDS, START_DATE, LATEST_END_DATE);
        Internship internship = new Internship();
        internship.setInterns(mentorList);
        when(internshipRepository.findById(internshipDto.id())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> validator.validateCountOfInterns(internshipDto));
    }
}