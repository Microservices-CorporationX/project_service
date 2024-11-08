package faang.school.projectservice.validator;

import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationValidatorTest {

    @Mock
    StageInvitationJpaRepository repository;

    @InjectMocks
    StageInvitationValidator stageInvitationValidator;

    @Test
    public void throwAlreadyExistsExceptionTest() {
        long id = 1L;

        assertThrows(AlreadyExistsException.class,
                () -> stageInvitationValidator.validateStageInvitationExists(id));
    }

    @Test
    public void dontThrowAlreadyExistsExceptionTest() {
        long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new StageInvitation()));

        assertDoesNotThrow(() -> stageInvitationValidator.validateStageInvitationExists(id));
    }

    @Test
    public void throwEntityNotFoundExceptionTest() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> stageInvitationValidator.validateStageInvitationNotExists(id));
    }

    @Test
    public void dontThrowEntityNotFoundExceptionTest() {
        long id = 1L;
        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(stageInvitation));

        StageInvitation result = stageInvitationValidator.validateStageInvitationNotExists(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void isInvitationSendToThisTeamMemberTest() {
        long teamMemberId = 1L;
        long stageInvitationId = 2L;

        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberId);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setInvited(teamMember);

        when(repository.findById(stageInvitationId)).thenReturn(Optional.of(stageInvitation));

        assertDoesNotThrow(
                () -> stageInvitationValidator.validateIsInvitationSentToThisTeamMember(
                        teamMemberId, stageInvitationId));
    }

    @Test
    public void isInvitationSendToThisTeamMemberThrowsExceptionTest() {
        long teamMemberId = 1L;
        long secondTeamMemberId = 101L;
        long stageInvitationId = 2L;

        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberId);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setInvited(teamMember);

        when(repository.findById(stageInvitationId)).thenReturn(Optional.of(stageInvitation));

        assertThrows(DataValidationException.class,
                () -> stageInvitationValidator.validateIsInvitationSentToThisTeamMember(
                        secondTeamMemberId, stageInvitationId));
    }
}