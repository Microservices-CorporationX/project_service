package faang.school.projectservice.validator.meet;

import faang.school.projectservice.model.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeetValidatorTest {
    private final MeetValidator meetValidator = new MeetValidator();
    private Meet meet;

    @BeforeEach
    void setUp() {
        meet = Meet.builder()
                .id(1L)
                .title("Team Meeting")
                .creatorId(1L)
                .build();
    }

    @Test
    void validateMeetToUpdate_shouldNotThrowException_whenCreatorIdMatches() {
        assertDoesNotThrow(() -> meetValidator.validateMeetToUpdate(meet, 1L));
    }

    @Test
    void validateMeetToUpdate_shouldThrowException_whenCreatorIdDoesNotMatch() {
        assertThrows(RuntimeException.class, () -> meetValidator.validateMeetToUpdate(meet, 2L),
                "Only creator can update meet");
    }

    @Test
    void validateMeetToDelete_shouldNotThrowException_whenCreatorIdMatches() {
        assertDoesNotThrow(() -> meetValidator.validateMeetToDelete(meet, 1L));
    }

    @Test
    void validateMeetToDelete_shouldThrowException_whenCreatorIdDoesNotMatch() {
        assertThrows(RuntimeException.class, () -> meetValidator.validateMeetToDelete(meet, 2L),
                "Only creator can delete meet and participants");
    }

}