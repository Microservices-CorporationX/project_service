package faang.school.projectservice.service.validator;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public void validateEqualsId(long authorId, long invitedId) {
        if (authorId == invitedId) {
            throw new BusinessException(String.format(
                    "Автор приглашения на этап и приглашенный на этот этап не могут быть одним человеком. " +
                            "\n authorId: %d\n invitedId: %d", authorId, invitedId));
        }
    }

    public void validateInvitedMemberTeam(long authorId, long invitedId) {
        Optional<TeamMember> authorTeamMemberOpt = teamMemberRepository.findById(authorId);
        Optional<TeamMember> invitedTeamMemberOpt = teamMemberRepository.findById(invitedId);

        if (authorTeamMemberOpt.isEmpty()) {
            throw new EntityNotFoundException("Автор не найден в базе данных.");
        }
        if (invitedTeamMemberOpt.isEmpty()) {
            throw new EntityNotFoundException("Приглашенный не найден в базе данных.");
        }

        Long authorTeamId = authorTeamMemberOpt.get().getTeam().getId();
        Long invitedTeamId = invitedTeamMemberOpt.get().getTeam().getId();

        if (!authorTeamId.equals(invitedTeamId)) {
            throw new BusinessException(String.format(
                    "Автор приглашения и приглашенный должны быть в одной команде." +
                            "\nauthorTeamId: %d invitedTeamId: %d", authorTeamId, invitedTeamId));
        }
    }
}
