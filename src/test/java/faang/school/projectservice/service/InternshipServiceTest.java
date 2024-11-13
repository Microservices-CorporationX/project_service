package faang.school.projectservice.service;

import faang.school.projectservice.dto.internShip.InternshipCreatedDto;
import faang.school.projectservice.dto.internShip.InternshipGetAllDto;
import faang.school.projectservice.dto.internShip.InternshipGetByIdDto;
import faang.school.projectservice.dto.internShip.InternshipUpdatedDto;
import faang.school.projectservice.handler.InternshipCompletionHandler;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private ProjectService projectService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipValidator internshipValidator;

    @Mock
    private InternshipCompletionHandler completionHandler;

    @Mock
    private InternshipMapper internshipMapper;

    private InternshipCreatedDto createdDto;
    private InternshipUpdatedDto updatedDto;
    private Internship internship;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createdDto = new InternshipCreatedDto();
        createdDto.setStartDate(LocalDateTime.now());
        createdDto.setEndDate(LocalDateTime.now().plusMonths(1));

        updatedDto = new InternshipUpdatedDto();
        updatedDto.setId(1L);
        updatedDto.setStatus(InternshipStatus.COMPLETED);

        internship = new Internship();
        internship.setId(1L);
    }

    @Test
    public void testCreateValidateAndSaveNewInternship() {
        when(internshipMapper.createInternship(createdDto)).thenReturn(internship);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);
        when(internshipMapper.toCreatedDto(internship)).thenReturn(createdDto);

        InternshipCreatedDto result = internshipService.createInternship(createdDto);

        verify(internshipValidator).durationValidate(createdDto);
        verify(projectService).getProjectTeamMembersIds(createdDto);
        verify(internshipRepository).save(internship);
        verify(internshipMapper).toCreatedDto(internship);

        assertEquals(createdDto, result);
    }

    @Test
    void testUpdateInternship() {
        InternshipUpdatedDto updatedDto = new InternshipUpdatedDto();
        updatedDto.setId(1L);

        Internship internship = new Internship();
        Internship savedInternship = new Internship();

        when(internshipRepository.findById(updatedDto.getId())).thenReturn(Optional.of(internship));
        when(internshipMapper.toEntity(updatedDto)).thenReturn(internship);
        when(internshipRepository.save(internship)).thenReturn(savedInternship);
        when(internshipMapper.toUpdatedDto(savedInternship)).thenReturn(updatedDto);

        InternshipUpdatedDto result = internshipService.updateInternship(updatedDto);

        verify(completionHandler).internsToDismissal(updatedDto.getInterns());
        verify(completionHandler).processInternshipCompletion(internship, internship.getStatus());
        verify(internshipRepository).save(internship);
        assertEquals(updatedDto, result);
    }

    @Test
    void testGetAllInternships() {
        List<Internship> internships = List.of(new Internship());
        InternshipGetAllDto dto = new InternshipGetAllDto();

        when(internshipRepository.findAll()).thenReturn(internships);
        when(internshipMapper.toGetAllDto(any(Internship.class))).thenReturn(dto);

        List<InternshipGetAllDto> result = internshipService.getAllInternships();

        verify(internshipRepository).findAll();
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetByIdInternship() {
        long internshipId = 1L;
        Internship internship = new Internship();
        InternshipGetByIdDto dto = new InternshipGetByIdDto();

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));
        when(internshipMapper.toGetByIdDto(internship)).thenReturn(dto);

        InternshipGetByIdDto result = internshipService.getByIdInternship(internshipId);

        verify(internshipRepository).findById(internshipId);
        assertEquals(dto, result);
    }

    @Test
    void testGetByIdInternship_NotFound() {
        long internshipId = 1L;

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.getByIdInternship(internshipId));
    }

}
