package faang.school.projectservice.filter.stage_invitation_filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationProjectNameFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter != null &&
                filter.getProjectNamePattern() != null &&
                !filter.getProjectNamePattern().isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, StageInvitationFilterDto filter) {
        return invitations.filter(
                stageInvitation -> stageInvitation.getStage().getProject().getName().toLowerCase().
                        contains(filter.getProjectNamePattern().toLowerCase()));
    }
}
