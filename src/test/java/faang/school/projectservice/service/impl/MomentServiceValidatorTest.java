package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.service.ProjectService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentServiceValidatorTest {
    @Mock
    ProjectService projectService;
    @Spy
    ProjectMapperImpl projectMapper;
    @InjectMocks
    MomentServiceValidator momentServiceValidator;

    private MomentCreateRequestDto validMomentCreateRequestDto;
    private MomentCreateRequestDto notValidMomentCreateRequestDto;
    private MomentUpdateRequestDto validMomentUpdateRequestDto;

    @BeforeEach
    void setUp() {

        validMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .projectIds(List.of(1L, 2L, 3L))
                .teamMemberIds(List.of(10L, 20L, 30L))
                .build();

        notValidMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .description("It's a very cool moment")
                .teamMemberIds(List.of(10L, 20L, 30L))
                .build();

        validMomentUpdateRequestDto = MomentUpdateRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .projectToAddIds(List.of(1L, 2L, 3L))
                .teamMemberToAddIds(List.of(10L, 20L, 30L))
                .build();
    }

    @Test
    @DisplayName("Test active projects")
    void testValidateActiveProjects() {
        List<ProjectResponseDto> someActiveProjects = TestData.getSomeActiveProjects().stream()
                .map(project -> projectMapper.toProjectResponseDto(project))
                .toList();
        Mockito.when(projectService.getProjectsByIds(Mockito.anyList())).thenReturn(someActiveProjects);
        momentServiceValidator.validateMomentProjectIds(validMomentCreateRequestDto.projectIds());
    }

    @Test
    @DisplayName("Test non-active projects")
    void testValidateNonActiveProjects() {
        List<ProjectResponseDto> someNonActiveProjects = TestData.getSomeNotActiveProjects().stream()
                .map(project -> projectMapper.toProjectResponseDto(project))
                .toList();
        Mockito.when(projectService.getProjectsByIds(Mockito.anyList())).thenReturn(someNonActiveProjects);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMomentProjectIds(validMomentCreateRequestDto.projectIds()));
    }

    @Test
    @DisplayName("Test name of moments")
    void testValidateName() {
        momentServiceValidator.validateMomentName(validMomentUpdateRequestDto.name());
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMomentName(notValidMomentCreateRequestDto.name()));
    }

}