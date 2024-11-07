package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageInvitationValidatorTest {
    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private StageInvitationValidator validator;

    @Test
    void validateStageInvitation_ShouldThrowException_WhenStageIdNotFound() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setStageId(1L);
        when(stageService.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateStageInvitation(dto));
    }

    @Test
    void validateStageInvitation_ShouldThrowException_WhenAuthorIdNotFound() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(2L);
        when(teamMemberService.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateStageInvitation(dto));
    }

    @Test
    void validateStageInvitation_ShouldThrowException_WhenInvitedIdNotFound() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setInvitedId(3L);
        when(teamMemberService.existsById(3L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateStageInvitation(dto));
    }

    @Test
    void validateStageInvitation_ShouldPass_WhenAllDataIsValid() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setStageId(1L);
        dto.setAuthorId(2L);
        dto.setInvitedId(3L);
        dto.setStatus(StageInvitationStatus.PENDING);

        when(stageService.existsById(1L)).thenReturn(true);
        when(teamMemberService.existsById(2L)).thenReturn(true);
        when(teamMemberService.existsById(3L)).thenReturn(true);

        validator.validateStageInvitation(dto);
    }
}