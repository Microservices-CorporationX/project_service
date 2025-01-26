package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentValidatorTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private MomentValidator momentValidator;

    private final Project createdProject = Project.builder().id(1L).status(ProjectStatus.CREATED).build();
    private final Project completedProject = Project.builder().id(3L).status(ProjectStatus.COMPLETED).build();
    private final Project cancelledProject = Project.builder().id(4L).status(ProjectStatus.CANCELLED).build();
    private final Project holdProject = Project.builder().id(5L).status(ProjectStatus.ON_HOLD).build();
    private final List<Project> projects = List.of(
            createdProject,
            completedProject,
            cancelledProject,
            holdProject
    );
    private final List<Project> activeProjects = List.of(createdProject);
    private final List<Long> usersIds = List.of(1L, 2L);
    private final List<Long> projectIds = projects.stream().map(Project::getId).toList();
    private final List<Long> activeProjectIds = activeProjects.stream().map(Project::getId).toList();

    @Test
    public void shouldSuccessValidateThatProjectsAreActive() {
        when(projectRepository.findAllById(projectIds)).thenReturn(List.of(createdProject));

        momentValidator.validateThatProjectsAreActive(projectIds);

        verify(projectRepository).findAllById(projectIds);
    }

    @Test
    public void shouldThrowExceptionWhenHaveInactiveProjects() {
        when(projectRepository.findAllById(projectIds)).thenReturn(projects);

        BusinessException exception = assertThrows(BusinessException.class, () -> momentValidator.validateThatProjectsAreActive(projectIds));
        assertEquals("Нельзя создать момент для неактивного проекта", exception.getMessage());
    }

    @Test
    public void shouldSuccessIfUsersAreExist() {
        momentValidator.validateThatUserIdsExist(usersIds);

        verify(teamMemberService).areTeamMembersExist(usersIds);
    }

    @ParameterizedTest
    @MethodSource("getInputUserIds")
    public void testValidateThatUserIdsExist_IfInputUsersIdsAreNullOrEmpty_DoNothing(List<Long> userIds) {
        momentValidator.validateThatUserIdsExist(userIds);

        verifyNoMoreInteractions(teamMemberService);
    }

    private static Stream<List<Long>> getInputUserIds() {
        return Stream.of(
                null,
                List.of()
        );
    }

    @Test
    public void shouldThrowExceptionIfUsersAreNotExist() {
        doAnswer(invocation -> {
            throw new BusinessException("Участника команды с userId=" + usersIds.stream().findFirst() + " не существует");
        }).when(teamMemberService).areTeamMembersExist(usersIds);

        assertThrows(BusinessException.class, () -> momentValidator.validateThatUserIdsExist(usersIds));
    }

    @Test
    public void shouldSuccessValidateMomentUpdateDto_IfValidProjectIds() {
        MomentUpdateDto dto = MomentUpdateDto.builder().projectIds(activeProjectIds).build();
        when(projectRepository.findAllById(dto.getProjectIds())).thenReturn(activeProjects);

        momentValidator.validateMomentUpdateDto(dto);
        verify(projectRepository).findAllById(activeProjectIds);
    }

    @Test
    public void shouldSuccessValidateMomentUpdateDto_IfValidUserIds() {
        MomentUpdateDto dto = MomentUpdateDto.builder().userIds(usersIds).build();

        momentValidator.validateMomentUpdateDto(dto);
        verify(teamMemberService).areTeamMembersExist(usersIds);
    }

    @Test
    public void shouldSuccessValidateMomentUpdateDto_IfValidDto() {
        MomentUpdateDto dto = MomentUpdateDto.builder().userIds(usersIds).projectIds(activeProjectIds).build();
        when(projectRepository.findAllById(dto.getProjectIds())).thenReturn(activeProjects);

        momentValidator.validateMomentUpdateDto(dto);
        verify(projectRepository).findAllById(activeProjectIds);
        verify(teamMemberService).areTeamMembersExist(usersIds);
    }

    @Test
    public void shouldThrowExceptionValidateMomentUpdateDto_IfInvalidUserIds() {
        MomentUpdateDto dto = MomentUpdateDto.builder().userIds(usersIds).build();
        String expectedMessage = "Участника команды с userId=" + usersIds.stream().findFirst() + " не существует";
        doAnswer(invocation -> {
            throw new BusinessException(expectedMessage);
        }).when(teamMemberService).areTeamMembersExist(usersIds);

        BusinessException ex = assertThrows(BusinessException.class, () -> momentValidator.validateMomentUpdateDto(dto));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void shouldThrowExceptionValidateMomentUpdateDto_IfInvalidProjectIds() {
        MomentUpdateDto dto = MomentUpdateDto.builder().projectIds(List.of(cancelledProject.getId())).build();
        when(projectRepository.findAllById(dto.getProjectIds())).thenReturn(List.of(cancelledProject));

        BusinessException ex = assertThrows(BusinessException.class, () -> momentValidator.validateMomentUpdateDto(dto));
        assertEquals("Нельзя создать момент для неактивного проекта", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getInvalidMomentUpdateDtos")
    public void shouldThrowExceptionValidateMomentUpdateDto_IfInvalidDto(MomentUpdateDto dto) {
        BusinessException ex = assertThrows(BusinessException.class, () -> momentValidator.validateMomentUpdateDto(dto));
        assertEquals("userIds или projectIds должны быть заполнены", ex.getMessage());
    }

    private static Stream<MomentUpdateDto> getInvalidMomentUpdateDtos() {
        return Stream.of(
                MomentUpdateDto.builder().projectIds(List.of()).userIds(List.of()).build(),
                MomentUpdateDto.builder().projectIds(null).userIds(null).build(),
                MomentUpdateDto.builder().projectIds(List.of()).userIds(null).build(),
                MomentUpdateDto.builder().projectIds(null).userIds(List.of()).build()
        );
    }
}