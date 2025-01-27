package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentControllerValidatorTest {
    @InjectMocks
    MomentControllerValidator momentControllerValidator;
    MomentRequestDto validMomentRequestDto;
    MomentRequestDto emptyNameMomentRequestDto;
    MomentRequestDto emptyProjectIdsMomentRequestDto;

    @BeforeEach
    void setUp() {
        validMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .date("25/01/2025 12:11:10")
                .projectToAddIds(List.of(1L, 2L, 3L))
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();
        emptyNameMomentRequestDto = MomentRequestDto.builder()
                .description("It's a very cool moment")
                .date("25/01/2025 12:11:10")
                .projectToAddIds(List.of(1L, 2L, 3L))
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();
        emptyProjectIdsMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .date("25/01/2025 12:11:10")
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();
    }

    @Test
    @DisplayName("Test names of DTO")
    void testValidateNameMomentRequestDto() {
        momentControllerValidator.validateName(validMomentRequestDto);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentControllerValidator.validateName(emptyNameMomentRequestDto));
    }

    @Test
    @DisplayName("Test project ids of DTO")
    void testValidateProjectIds() {
        momentControllerValidator.validateProjectIds(validMomentRequestDto);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentControllerValidator.validateProjectIds(emptyProjectIdsMomentRequestDto));
    }
}