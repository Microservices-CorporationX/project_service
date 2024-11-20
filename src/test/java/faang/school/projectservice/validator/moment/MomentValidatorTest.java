package faang.school.projectservice.validator.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {

    @InjectMocks
    private MomentValidator momentValidator;

    @Mock
    private MomentRepository momentRepository;

    @Test
    void testValidateUniqueMoment(){
        MomentDto momentDto = MomentDto.builder().id(1L).build();
        when(momentRepository.findById(momentDto.getId())).thenReturn(Optional.of(new Moment()));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> momentValidator.validateUniqueMoment(momentDto));
        assertEquals("Moment with id " + momentDto.getId() + " already exists", exception.getMessage());
    }

    @Test
    void testValidateActiveMoment(){
        Moment momentCompleted = new Moment();
        momentCompleted.setId(1L);
        momentCompleted.setProjects(new ArrayList<>(List.of(Project.builder().status(ProjectStatus.COMPLETED).build())));
        Moment momentOnHold = new Moment();
        momentOnHold.setId(2L);
        momentOnHold.setProjects(new ArrayList<>(List.of(Project.builder().status(ProjectStatus.ON_HOLD).build())));
        Moment momentCancelled = new Moment();
        momentCancelled.setId(3L);
        momentCancelled.setProjects(new ArrayList<>(List.of(Project.builder().status(ProjectStatus.CANCELLED).build())));
        Moment momentInProgress = new Moment();
        momentInProgress.setId(4L);
        momentInProgress.setProjects(new ArrayList<>(List.of(Project.builder().status(ProjectStatus.IN_PROGRESS).build())));

        DataValidationException exception1 = assertThrows(DataValidationException.class,
                () -> momentValidator.validateActiveMoment(momentCompleted));
        assertEquals("Moment id: " + momentCompleted.getId() + " cannot be created for inactive projects"
                , exception1.getMessage());
        DataValidationException exception2 = assertThrows(DataValidationException.class,
                () -> momentValidator.validateActiveMoment(momentOnHold));
        assertEquals("Moment id: " + momentOnHold.getId() + " cannot be created for inactive projects"
                , exception2.getMessage());
        DataValidationException exception3 = assertThrows(DataValidationException.class,
                () -> momentValidator.validateActiveMoment(momentCancelled));
        assertEquals("Moment id: " + momentCancelled.getId() + " cannot be created for inactive projects"
                , exception3.getMessage());
        momentValidator.validateActiveMoment(momentInProgress);
    }

    @Test
    void testValidateMomentExists(){
        long momentId = 1L;
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> momentValidator.validateMomentExists(momentId));
        assertEquals("Moment with id " + momentId + " does not exist"
                , exception.getMessage());
    }

}