package ru.corporationx.projectservice.validator.stageinvitation;

import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.repository.jpa.StageInvitationJpaRepository;
import ru.corporationx.projectservice.model.entity.stage.Stage;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;
import ru.corporationx.projectservice.service.stage.StageService;
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