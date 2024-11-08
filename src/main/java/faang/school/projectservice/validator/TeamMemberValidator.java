package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidator {

    TeamMemberJpaRepository repository;

    public TeamMember validateTeamMemberExists(long teamMemberId) {
        return repository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("TeamMember", teamMemberId));
    }

    public TeamMember validateIsTeamMemberParticipantOfProject(long invitedId, StageInvitation invitation) {
        TeamMember teamMember = repository.findById(invitedId)
                .orElseThrow(() -> new EntityNotFoundException("TeamMember", invitedId));
        Project invitationProject = invitation.getStage().getProject();

        boolean isParticipant = teamMember.getStages().stream()
                .anyMatch(stage -> stage.getProject().equals(invitationProject));

        if (!isParticipant) {
            throw new DataValidationException("This team member is not participant of this project");
        }
        return teamMember;
    }
}
