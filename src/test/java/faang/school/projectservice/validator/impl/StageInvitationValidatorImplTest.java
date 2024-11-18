package faang.school.projectservice.validator.impl;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.abstracts.StageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationValidatorImplTest {
    @Mock
    private StageInvitationJpaRepository stageInvitationRepository;
    @Mock
    private StageService stageService;
    @InjectMocks
    StageInvitationValidatorImpl invitationValidator;

    @Test
    public void testGetStageInvitationWithInvalidId() {
        Long invitationId = 1L;
        when(stageInvitationRepository.findById(invitationId))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> invitationValidator.getStageInvitation(invitationId));

        assertEquals("Stage invitation doesn't exist", thrown.getMessage());
    }

    @Test
    public void testValidationAuthorAndInvited() {
        Long authorId = 1L;

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> invitationValidator.validateAuthorAndInvited(authorId, authorId));

        assertEquals("authorId and invitedId must be different", thrown.getMessage());
    }

    @Test
    public void testValidationInvitationDoesNotExist() {
        Long invitedId = 1L;
        Long stageId = 2L;
        TeamMember invited = TeamMember.builder().id(invitedId).build();
        Stage stage = Stage.builder().stageId(stageId).build();
        when(stageInvitationRepository.existsByInvitedAndStage(invited, stage))
                .thenReturn(true);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> invitationValidator.validateInvitationDoesNotExist(invited, stage));

        assertEquals("This invitation already exists", thrown.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideInvitationStatus")
    public void testValidationInvitationStatus(StageInvitationStatus requiredStatus, String exceptionMessage) {
        StageInvitation stageInvitation = StageInvitation.builder()
                .status(requiredStatus)
                .build();

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> invitationValidator.validateInvitationStatus(stageInvitation, requiredStatus));

        assertEquals(exceptionMessage, thrown.getMessage());
    }

    private static Stream<Arguments> provideInvitationStatus() {
        return Stream.of(
                Arguments.of(StageInvitationStatus.ACCEPTED, "Invitation has already been accepted"),
                Arguments.of(StageInvitationStatus.REJECTED, "Invitation has already been rejected")
        );
    }
}