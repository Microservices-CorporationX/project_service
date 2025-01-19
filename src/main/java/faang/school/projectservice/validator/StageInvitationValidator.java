package faang.school.projectservice.validator;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateStatusPendingCheck(StageInvitation stageInvitation) {
        if (stageInvitation.getStatus().equals(StageInvitationStatus.ACCEPTED)) {
            throw new BusinessException(String.format(
                    "Приглашение присоединиться к этапу с id: %s уже принято", stageInvitation.getId())
            );
        }
        if (stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED)) {
            throw new BusinessException(String.format(
                    "Приглашение присоединиться к этапу с id: %s уже отклонено", stageInvitation.getId())
            );
        }
    }

    public void validateEqualsId(Long authorId, Long invitedId) {
        if (authorId.equals(invitedId)) {
            throw new BusinessException(String.format(
                    "Автор приглашения на этап и приглашенный на этот этап не могут быть одним человеком. " +
                            "\n authorId: %d\n invitedId: %d", authorId, invitedId));
        }
    }

    public void validateInvitedMemberTeam(Long authorId, Long invitedId) {
        Long authorTeamId = teamMemberRepository.findById(authorId).get().getTeam().getId();
        Long invitedTeamId = teamMemberRepository.findById(invitedId).get().getTeam().getId();

        if (!authorTeamId.equals(invitedTeamId)) {
            throw new BusinessException(String.format(
                    "Автор приглашения на этап и приглашенный должны быть в одной команде." +
                            "\n authorTeamId: \n%d  invitedTeamId: %d", authorTeamId, invitedTeamId));
        }
    }
}
