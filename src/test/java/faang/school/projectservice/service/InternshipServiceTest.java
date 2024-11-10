package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.dto.client.internShip.InternshipUpdatedDto;
import faang.school.projectservice.handler.InternshipCompletionHandler;
import faang.school.projectservice.mapper.InternshipCreateMapper;
import faang.school.projectservice.mapper.InternshipUpdateMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipDurationValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
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
    private InternshipCreateMapper internShipCreateMapper;

    @Mock
    private InternshipDurationValidator internshipDurationValidator;

    @Mock
    private InternshipUpdateMapper internShipUpdateMapper;

    @Mock
    private InternshipCompletionHandler completionHandler;


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
    void testCreatValidateAndSaveNewInternship() {
        when(internShipCreateMapper.toEntity(createdDto)).thenReturn(internship);
        when(internShipCreateMapper.toDto(internship)).thenReturn(createdDto);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);

        InternshipCreatedDto result = internshipService.createInternship(createdDto);

        verify(internshipDurationValidator).durationValidate(createdDto);
        verify(projectService).getProjectTeamMembersIds(createdDto);
        verify(internshipRepository).save(internship);
        assertEquals(createdDto, result);
    }

    @Test
    void testToUpdateInternshipInternshipAndHandleCompletion() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(internShipUpdateMapper.toEntity(updatedDto)).thenReturn(internship);
        when(internShipUpdateMapper.toDto(internship)).thenReturn(updatedDto);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);

        InternshipUpdatedDto result = internshipService.updateInternship(updatedDto);

        verify(completionHandler).handleInternsCompletion(internship);
        verify(internshipRepository).save(internship);
        assertEquals(updatedDto, result);
    }

    @Test
    void updateInternship_ShouldThrowEntityNotFoundExceptionWhenInternshipNotFound() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> internshipService.updateInternship(updatedDto));
    }
}
