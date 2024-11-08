package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberValidatorTest {

    @Mock
    TeamMemberJpaRepository repository;

    @InjectMocks
    TeamMemberValidator validator;

    @Test
    public void teamMemberNotExistsTest() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> validator.validateTeamMemberExists(id));
    }

    @Test
    public void teamMemberExistsTest() {
        long id = 1L;
        TeamMember teamMember = new TeamMember();
        when(repository.findById(id)).thenReturn(Optional.of(teamMember));

        TeamMember result = validator.validateTeamMemberExists(id);

        assertEquals(teamMember, result);
    }

    @Test
    public void teamMemberParticipantOfProjectTest() {
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

        when(repository.findById(memberId)).thenReturn(Optional.of(teamMember));

        TeamMember result = validator.validateIsTeamMemberParticipantOfProject(memberId, stageInvitation);

        assertEquals(teamMember, result);
    }

    @Test
    public void teamMemberIsNotParticipantOfProjectTest() {
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

        when(repository.findById(memberId)).thenReturn(Optional.of(teamMember));

        TeamMember result = validator.validateIsTeamMemberParticipantOfProject(memberId, stageInvitation);

        assertNotEquals(anotherTeamMember, result);
        assertThrows(DataValidationException.class,
                () -> validator.validateIsTeamMemberParticipantOfProject(memberId, anotherStageInvitation));
    }
}