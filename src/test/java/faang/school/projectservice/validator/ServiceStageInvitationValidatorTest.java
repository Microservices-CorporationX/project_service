package faang.school.projectservice.validator;

import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.team_member.TeamMemberService;
import faang.school.projectservice.validator.stage_invitation.ServiceStageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceStageInvitationValidatorTest {
    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @InjectMocks
    ServiceStageInvitationValidator serviceStageInvitationValidator;

    private Long invitedId;
    private Long stageInvitationId;
    private String rejection;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        invitedId = 1L;
        stageInvitationId = 2L;
    }

    @Test
    void testCheckWhetherThisRequestExists_InvitationExists() {
        when(stageInvitationRepository.stageInvitationExist(stageInvitationId)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            serviceStageInvitationValidator.checkWhetherThisRequestExists(stageInvitationId);
        });

        assertEquals("This invitation already exists", exception.getMessage());

        verify(stageInvitationRepository).stageInvitationExist(stageInvitationId);
    }

    @Test
    void testCheckWhetherThisRequestExists_InvitationDoesNotExist() {
        when(stageInvitationRepository.stageInvitationExist(stageInvitationId)).thenReturn(false);

        assertDoesNotThrow(() -> {
            serviceStageInvitationValidator.checkWhetherThisRequestExists(stageInvitationId);
        });

        verify(stageInvitationRepository).stageInvitationExist(stageInvitationId);
    }

    @Test
    void testCheckTheExistenceOfTheInvitee_InviteeExist() {
        when(teamMemberService.existedTeamMember(invitedId)).thenReturn(true);

        assertDoesNotThrow(() -> {
            serviceStageInvitationValidator.checkTheExistenceOfTheInvitee(invitedId);
        });

        verify(teamMemberService).existedTeamMember(invitedId);
    }

    @Test
    void testCheckTheExistenceOfTheInvitee_InviteeDoesNotExist() {
        when(teamMemberService.existedTeamMember(invitedId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            serviceStageInvitationValidator.checkTheExistenceOfTheInvitee(invitedId);
        });

        assertEquals("This team member does not exist", exception.getMessage());

        verify(teamMemberService).existedTeamMember(invitedId);
    }

    @Test
    void checkTheReasonForTheFailure_RejectionIsExist() {
        rejection = "Rejection";
    }

    @Test
    void checkTheReasonForTheFailure_RejectionIsBlank() {
        rejection = "  ";

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            serviceStageInvitationValidator.checkTheReasonForTheFailure(rejection);
        });

        assertEquals("The reason for the refusal must be indicated", exception.getMessage());
    }

    @Test
    void checkTheReasonForTheFailure_RejectionIsEmpty() {
        rejection = null;

         DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            serviceStageInvitationValidator.checkTheReasonForTheFailure(rejection);
        });

        assertEquals("The reason for the refusal must exist", exception.getMessage());
    }
}
