package faang.school.projectservice.validator.team_member;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TeamMemberValidatorTest {

    private final TeamMemberValidator teamMemberValidator = new TeamMemberValidator();

    @Test
    public void teamMemberParticipantOfProjectTest() {
        ArgumentCaptor<TeamMember> captor = ArgumentCaptor.forClass(TeamMember.class);
        long memberId = 1L;
        long projectId = 10L;
        Project project = new Project();
        project.setId(projectId);

        Stage stage = new Stage();
        stage.setProject(project);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setStage(stage);

        TeamMember teamMember = new TeamMember();
        teamMember.setStages(new ArrayList<>(List.of(stage)));

        teamMemberValidator.validateIsTeamMemberParticipantOfProject(captor.capture(), stageInvitation);
        TeamMember result = captor.getValue();  //???
        assertEquals(teamMember, result);
    }

    @Test
    public void teamMemberIsNotParticipantOfProjectTest() {
        ArgumentCaptor<TeamMember> captor = ArgumentCaptor.forClass(TeamMember.class);
        long memberId = 1L;
        long projectId = 10L;
        long secondProjectId = 15L;
        Project project = new Project();
        project.setId(projectId);
        Project anotherProject = new Project();
        project.setId(secondProjectId);

        Stage stage = new Stage();
        stage.setProject(project);
        Stage anotherStage = new Stage();
        anotherStage.setProject(anotherProject);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setStage(stage);
        StageInvitation anotherStageInvitation = new StageInvitation();
        anotherStageInvitation.setStage(anotherStage);

        TeamMember teamMember = new TeamMember();
        teamMember.setStages(new ArrayList<>(List.of(stage)));
        TeamMember anotherTeamMember = new TeamMember();
        anotherTeamMember.setStages(new ArrayList<>(List.of(anotherStage)));

        teamMemberValidator.validateIsTeamMemberParticipantOfProject(captor.capture(), stageInvitation);
        TeamMember result = captor.getValue();
        assertNotEquals(anotherTeamMember, result);
        assertThrows(DataValidationException.class,
                () -> teamMemberValidator.validateIsTeamMemberParticipantOfProject(
                        teamMember, anotherStageInvitation)); //?????
    }
}