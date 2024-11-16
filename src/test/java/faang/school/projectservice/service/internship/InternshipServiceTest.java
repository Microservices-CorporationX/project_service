package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.internship.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private InternshipMapperImpl internshipMapper;
    @Mock
    private InternshipValidator internshipValidator;
    @Mock
    private TaskStatusValidator taskStatusValidator;
    @Mock
    private List<InternshipFilter> filters;

    @BeforeEach
    void setUp() {
        InternshipFilter internshipFilterMock = Mockito.mock(InternshipFilter.class);
        filters = List.of(internshipFilterMock);
    }

    @Test
    void testSuccessfulCreateInternship() {
        InternshipDto internshipDto = prepareInternshipDto();
        TeamMember firstIntern = TeamMember.builder()
                .id(1L)
                .build();
        TeamMember secondIntern = TeamMember.builder()
                .id(2L)
                .build();
        List<TeamMember> interns = List.of(firstIntern, secondIntern);
        when(teamMemberRepository.findAllById(internshipDto.getInternIds())).thenReturn(interns);

        InternshipDto internshipDtoAfterSave = internshipService.createInternship(internshipDto);

        assertEquals(internshipDto, internshipDtoAfterSave);
    }

    @Test
    void testUpdateNotFoundInternship() {
        InternshipDto internshipDto = prepareInternshipDto();
        when(internshipRepository.findById(internshipDto.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.updateInternship(internshipDto));

        assertEquals("Internship doesn't exist by id: 3", exception.getMessage());
    }

    @Test
    void testSuccessfulUpdateInternship() {
        InternshipDto internshipDto = prepareInternshipDto();
        TeamMember firstIntern = TeamMember.builder()
                .id(1L)
                .build();
        TeamMember secondIntern = TeamMember.builder()
                .id(2L)
                .build();
        List<TeamMember> interns = List.of(firstIntern, secondIntern);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(interns);
        when(internshipRepository.findById(internshipDto.getId())).thenReturn(Optional.of(internship));
        when(teamMemberRepository.findAllById(internshipDto.getInternIds())).thenReturn(interns);
        when(taskStatusValidator.checkingInternsTaskStatus(internship)).thenReturn(internship);

        InternshipDto internshipDtoAfterUpdate = internshipService.updateInternship(internshipDto);

        assertEquals(internshipDto, internshipDtoAfterUpdate);
    }

    @Test
    void testGetAllInternships() {
        List<Internship> internships = List.of(prepareInternshipEntity(), prepareInternshipEntity());
        List<InternshipDto> expectedInternships = internshipMapper.mapToDtoList(internships);
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> actualInternships = internshipService.getInternships();

        assertEquals(expectedInternships, actualInternships);
    }

    @Test
    void testGetAllInternshipsWithFilters() {
        Internship firstInternship = prepareInternshipEntity();
        Internship secondInternship = prepareInternshipEntity();
        List<Internship> internships = new ArrayList<>(List.of(firstInternship, secondInternship));
        List<InternshipDto> expectedDto = internshipMapper.mapToDtoList(internships);
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> actualDto = internshipService.getInternships(new InternshipFilterDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testGetInternshipByIdIfItDoesNotExist() {
        Internship internship = prepareInternshipEntity();
        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipService.getInternship(internship.getId()));

        assertEquals("Internship doesn't exist by id: 3", exception.getMessage());
    }

    @Test
    void testGetInternshipByIdIfItExists() {
        Internship internship = prepareInternshipEntity();
        InternshipDto expectedDto = internshipMapper.toDto(internship);
        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.of(internship));

        InternshipDto actualDto = internshipService.getInternship(internship.getId());

        assertEquals(expectedDto, actualDto);
    }

    private InternshipDto prepareInternshipDto() {
        return InternshipDto.builder()
                .id(3L)
                .internIds(List.of(1L, 2L))
                .mentorId(4L)
                .projectId(5L)
                .internshipStatus(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
    }

    private Internship prepareInternshipEntity() {
        TeamMember firstIntern = TeamMember.builder()
                .id(1L)
                .build();
        TeamMember secondIntern = TeamMember.builder()
                .id(2L)
                .build();
        TeamMember mentor = TeamMember.builder()
                .id(4L)
                .build();
        List<TeamMember> interns = List.of(firstIntern, secondIntern);
        return Internship.builder()
                .id(3L)
                .interns(interns)
                .mentorId(mentor)
                .project(new Project())
                .status(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
    }
}
