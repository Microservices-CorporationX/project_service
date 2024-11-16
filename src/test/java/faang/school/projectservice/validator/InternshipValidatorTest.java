package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.exception.InternshipDurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InternshipValidatorTest {
     private InternshipValidator internshipValidator;
     private InternshipCreatedDto internShipCreatedDto;

     @BeforeEach
     public void setUp() {
          internshipValidator = new InternshipValidator();
          internShipCreatedDto = mock(InternshipCreatedDto.class);
     }

     @Test
     public void testDurationValidateWhenDurationExceedsThreeMonths_ShouldThrowException() {
          LocalDateTime startDate = LocalDateTime.now();
          LocalDateTime endDate = startDate.plusMonths(4);

          when(internShipCreatedDto.getStartDate()).thenReturn(startDate);
          when(internShipCreatedDto.getEndDate()).thenReturn(endDate);

          assertThrows(InternshipDurationException.class, () -> {
               internshipValidator.durationValidate(internShipCreatedDto);
          });
     }

     @Test
     public void testDurationValidateWhenDurationIsWithinThreeMonths_ShouldNotThrowException() {
          LocalDateTime startDate = LocalDateTime.now();
          LocalDateTime endDate = startDate.plusMonths(2);

          when(internShipCreatedDto.getStartDate()).thenReturn(startDate);
          when(internShipCreatedDto.getEndDate()).thenReturn(endDate);

          assertDoesNotThrow(() -> {
               internshipValidator.durationValidate(internShipCreatedDto);
          });
     }
}
