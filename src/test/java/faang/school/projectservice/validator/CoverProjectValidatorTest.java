package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoverProjectValidatorTest {

    @InjectMocks
    private CoverProjectValidator coverProjectValidator;

    @Mock
    private UserContext userContext;

    private Project project;

    private long userId;

    @BeforeEach
    public void setUp() {
        project = successfulSetUp();
        userId = 1L;
    }
    @Test
    public void testValidateWithOneParamSuccessful() {
        when(userContext.getUserId()).thenReturn(userId);
        coverProjectValidator.validation(project);
    }

    @Test
    public void testValidateWithOneParamFailed() {
        project = failedSetUp();
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(IllegalStateException.class,
                () -> coverProjectValidator.validation(project));
    }

    @Test
    public void testValidateWithTwoParamSuccessful() {
        MultipartFile image = mock(MultipartFile.class);

        when(userContext.getUserId()).thenReturn(userId);
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/png");

        coverProjectValidator.validation(project, image);
    }

    @Test
    public void testValidateWithTwoParamImageIsEmpty() {
        MultipartFile image = mock(MultipartFile.class);

        when(userContext.getUserId()).thenReturn(userId);
        when(image.isEmpty()).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> coverProjectValidator.validation(project, image));
        assertEquals("The cover image is empty", exception.getMessage());
    }

    @Test
    public void testValidateWithTwoParamExpectedIsImage() {
        MultipartFile image = mock(MultipartFile.class);

        when(userContext.getUserId()).thenReturn(userId);
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("video");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> coverProjectValidator.validation(project, image));
        assertEquals("Expected is image", exception.getMessage());
    }

    private Project successfulSetUp() {
        Project project = new Project();

        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);

        team.setTeamMembers(List.of(teamMember, teamMember));
        project.setTeams(List.of(team, team));

        return project;
    }

    private Project failedSetUp() {
        Project project = new Project();

        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(2L);

        team.setTeamMembers(List.of(teamMember, teamMember));
        project.setTeams(List.of(team, team));

        return project;
    }
}
