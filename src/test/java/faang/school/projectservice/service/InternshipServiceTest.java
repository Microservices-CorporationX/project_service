package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private InternshipService internshipService;

    private Internship internship;
    private InternshipDto internshipDto;
    private List<TeamMember> teamMembers;

    @BeforeEach
    void setUp() {
        internship = new Internship();
        internship.setId(1L);

        internshipDto = InternshipDto.builder()
                .id(1L)
                .name("Test Internship")
                .role(TeamRole.INTERN)
                .projectId(1L)
                .status(InternshipStatus.IN_PROGRESS)
                .mentorId(1L)
                .internIds(List.of(1L, 2L))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(2))
                .build();

        teamMembers = new ArrayList<>();

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(1L);
        teamMember1.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
        teamMembers.add(teamMember1);

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(2L);
        teamMember2.setRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)));
        teamMembers.add(teamMember2);

        internship.setInterns(new ArrayList<>(List.of(teamMember1, teamMember2)));
    }

    @Test
    void createInternship() {
        when(internshipMapper.toEntity(internshipDto, teamMemberService, projectService)).thenReturn(internship);
        when(internshipRepository.save(internship)).thenReturn(internship);

        Long result = internshipService.createInternship(internshipDto);

        assertEquals(1L, result);
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    void createInternship_InvalidDuration() {
        InternshipDto invalidDto = InternshipDto.builder()
                .id(1L)
                .name("Test Internship")
                .role(TeamRole.INTERN)
                .projectId(1L)
                .status(InternshipStatus.IN_PROGRESS)
                .mentorId(1L)
                .internIds(List.of(1L, 2L))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(4))
                .build();

        assertThrows(DataValidationException.class, () -> internshipService.createInternship(invalidDto));
    }

    @Test
    void updateInternship_Complete() {
        when(projectService.isProjectComplete(internshipDto.projectId())).thenReturn(true);

        internshipService.updateInternship(internshipDto);

        verify(teamMemberService,
                times(1)).setTeamMembersRoleAndRemoveInternRole(internshipDto.internIds(), internshipDto.role());
    }

    @Test
    void updateInternship_NotComplete() {
        when(projectService.isProjectComplete(internshipDto.projectId())).thenReturn(false);
        when(internshipRepository.findById(internshipDto.id())).thenReturn(Optional.of(internship));
        when(teamMemberService.getAllTeamMembersByIds(internshipDto.internIds())).thenReturn(teamMembers);

        internshipService.updateInternship(internshipDto);

        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    void changeInternshipStatus() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        internshipService.changeInternshipStatus(1L, InternshipStatus.COMPLETED);

        assertEquals(InternshipStatus.COMPLETED, internship.getStatus());
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    void completeInternship() {
        TeamMember teamMember = teamMembers.get(0);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(teamMemberService.getTeamMemberById(1L)).thenReturn(teamMember);

        internshipService.completeInternship(1L, 1L);

        assertFalse(internship.getInterns().contains(teamMember));
        verify(teamMemberService, times(1)).removeTeamRole(teamMember, TeamRole.INTERN);
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    void getInternshipById_NotFound() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.getInternshipDtoById(1L));
    }

    @Test
    void getAllInternships() {
        List<Internship> internships = List.of(internship);
        when(internshipRepository.findAll()).thenReturn(internships);
        when(internshipMapper.toDtos(internships)).thenReturn(List.of(internshipDto));

        assertEquals(1, internshipService.getAllInternships().size());
        assertEquals(internshipDto, internshipService.getAllInternships().get(0));
    }
}
