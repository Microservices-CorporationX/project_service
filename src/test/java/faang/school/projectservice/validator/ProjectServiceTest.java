package faang.school.projectservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private final ProjectServiceValidator projectServiceValidator = new ProjectServiceValidator();

    @Test
    public void testIsMentorPresent_WhenMentorIsPresent_ShouldReturnTrue() {
        List<Long> memberIds = Arrays.asList(1L, 2L, 3L, 4L);
        long mentorId = 3L;

        boolean result = projectServiceValidator.isMentorPresent(memberIds, mentorId);

        assertTrue(result, "Mentor should be present in the list");
    }

    @Test
    public void testIsMentorPresent_WhenMentorIsNotPresent_ShouldReturnFalse() {
        List<Long> memberIds = Arrays.asList(1L, 2L, 3L, 4L);
        long mentorId = 5L;

        boolean result = projectServiceValidator.isMentorPresent(memberIds, mentorId);
        assertFalse(result, "Mentor should not be present in the list");
    }

    @Test
    public void testIsMentorPresent_WhenListIsEmpty_ShouldReturnFalse() {
        List<Long> memberIds = List.of();
        long mentorId = 1L;

        boolean result = projectServiceValidator.isMentorPresent(memberIds, mentorId);
        assertFalse(result, "Mentor should not be present in the empty list");
    }
}
