package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class StageInvitationInvitedFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filters) {
        return filters.getInvited() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitation, StageInvitationFilterDto filters) {
        return stageInvitation.filter(invited -> invited.getInvited().equals(filters.getInvited()));
    }
}
