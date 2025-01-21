package faang.school.projectservice.validator;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CampaignValidatorTest {

    private CampaignValidator campaignValidator;

    @BeforeEach
    public void setUp() {
        campaignValidator = new CampaignValidator();
    }

    @Test
    public void testValidateAuthorRoleAuthorIsProjectOwner() {
        // arrange
        long authorId = 5L;
        Project project = Project.builder()
                .ownerId(authorId)
                .build();

        // act and assert
        assertDoesNotThrow(() -> campaignValidator.validateAuthorRole(project, authorId));
    }

    @Test
    public void testValidateAuthorRoleAuthorIsManager() {
        // arrange
        long authorId = 5L;
        TeamMember teamMember = TeamMember.builder()
                .userId(authorId)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        Team team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();
        Project project = Project.builder()
                .ownerId(2L)
                .teams(List.of(team))
                .build();

        // act and assert
        assertDoesNotThrow(() -> campaignValidator.validateAuthorRole(project, authorId));
    }

    @Test
    public void testValidateAuthorRoleFails() {
        // arrange
        long authorId = 5L;
        Team team = Team.builder()
                .teamMembers(new ArrayList<>())
                .build();
        Project project = Project.builder()
                .ownerId(2L)
                .teams(List.of(team))
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> campaignValidator.validateAuthorRole(project, authorId));
    }

    @Test
    public void testValidateCampaignIsNotDeleted() {
        // arrange
        Campaign campaign = Campaign.builder()
                .deleted(false)
                .build();

        // act and assert
        assertDoesNotThrow(() -> campaignValidator.validateCampaignIsNotDeleted(campaign));
    }

    @Test
    public void testValidateCampaignIsNotDeletedFails() {
        // arrange
        Campaign campaign = Campaign.builder()
                .deleted(true)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> campaignValidator.validateCampaignIsNotDeleted(campaign));
    }
}
