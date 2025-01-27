package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentServiceValidatorTest {

    @Mock
    ProjectService projectService;
    @InjectMocks
    MomentServiceValidator momentServiceValidator;

    private MomentRequestDto validMomentRequestDto;
    private MomentRequestDto validWithIdMomentRequestDto;
    private MomentRequestDto emptyNameMomentRequestDto;
    //private MomentRequestDto emptyProjectIdsMomentRequestDto;
    private MomentRequestDto wrongDateMomentRequestDto;
    private MomentRequestDto wrongDateFormatMomentRequestDto;

    @BeforeEach
    void setUp() {
        validMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .date("25/01/2025 12:11:10")
                .projectToAddIds(List.of(1L, 2L, 3L))
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();
        validWithIdMomentRequestDto = MomentRequestDto.builder()
                .id(123L)
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
        /*emptyProjectIdsMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .date("25/01/2025 12:11:10")
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();*/
        wrongDateMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .date("33/01/2025 12:11:10")
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();
        wrongDateFormatMomentRequestDto = MomentRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .date("2025/01/25 12:11:10")
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();

        //momentServiceValidator = new MomentServiceValidator(projectService);
    }

    @Test
    @DisplayName("Test Id null or not null")
    void testValidateMomentIdNotNull() {
        momentServiceValidator.validateMomentIdNotNull(validWithIdMomentRequestDto);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMomentIdNotNull(validMomentRequestDto));
    }

    @Test
    @DisplayName("Test active and non-active projects")
    void testValidateActiveProjects() {
        Mockito.when(projectService.getProjectsByIds(Mockito.anyList())).thenReturn(TestData.getSomeActiveProjects());
        momentServiceValidator.validateMoment(validMomentRequestDto);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMoment(emptyNameMomentRequestDto));
    }

    @Test
    @DisplayName("Test name of moments")
    void testValidateName() {
        Mockito.when(projectService.getProjectsByIds(Mockito.anyList())).thenReturn(TestData.getSomeActiveProjects());
        momentServiceValidator.validateMoment(validMomentRequestDto);

        Mockito.when(projectService.getProjectsByIds(Mockito.anyList())).thenReturn(TestData.getSomeNotActiveProjects());
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMoment(validMomentRequestDto));
    }

    @Test
    @DisplayName("Test date of moments")
    void testValidateDate() {
        Mockito.when(projectService.getProjectsByIds(Mockito.anyList())).thenReturn(TestData.getSomeActiveProjects());
        momentServiceValidator.validateMoment(validMomentRequestDto);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMoment(wrongDateMomentRequestDto));

        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMoment(wrongDateFormatMomentRequestDto));
    }
}