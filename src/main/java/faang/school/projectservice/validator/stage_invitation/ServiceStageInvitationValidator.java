package faang.school.projectservice.validator.stage_invitation;

import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;

import faang.school.projectservice.service.team_member.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceStageInvitationValidator {
    private TeamMemberService teamMemberService;
    private StageInvitationService stageInvitationService;

    public void checkWhetherThisRequestExists(Long stageInvitationId) {
        boolean existedStageInvitation = stageInvitationService.stageInvitationExist(stageInvitationId);
        if (existedStageInvitation) {
            throw new DataValidationException("This invitation already exists");
        }
    }

    public void checkTheExistenceOfTheInvitee(Long invitedId) {
        boolean existedTeamMember = teamMemberService.existedTeamMember(invitedId);
        if (!existedTeamMember) {
            throw new DataValidationException("This team member does not exist");
        }
    }
}
