package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.exception.InternshipDurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InternshipDurationValidatorTest {
     private InternshipDurationValidator internshipDurationValidator;
     private InternshipCreatedDto internShipCreatedDto;

     @BeforeEach
     public void setUp() {
          internshipDurationValidator = new InternshipDurationValidator();
          internShipCreatedDto = mock(InternshipCreatedDto.class);
     }

     @Test
     public void testDurationValidateWhenDurationExceedsThreeMonths_ShouldThrowException() {
          LocalDateTime startDate = LocalDateTime.now();
          LocalDateTime endDate = startDate.plusMonths(4);

          when(internShipCreatedDto.getStartDate()).thenReturn(startDate);
          when(internShipCreatedDto.getEndDate()).thenReturn(endDate);

          assertThrows(InternshipDurationException.class, () -> {
               internshipDurationValidator.durationValidate(internShipCreatedDto);
          });
     }

     @Test
     public void testDurationValidateWhenDurationIsWithinThreeMonths_ShouldNotThrowException() {
          LocalDateTime startDate = LocalDateTime.now();
          LocalDateTime endDate = startDate.plusMonths(2);

          when(internShipCreatedDto.getStartDate()).thenReturn(startDate);
          when(internShipCreatedDto.getEndDate()).thenReturn(endDate);

          assertDoesNotThrow(() -> {
               internshipDurationValidator.durationValidate(internShipCreatedDto);
          });
     }
}
