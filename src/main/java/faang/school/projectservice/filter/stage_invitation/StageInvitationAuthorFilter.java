package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class StageInvitationAuthorFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filters) {
        return filters.getAuthor() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitation, StageInvitationFilterDto filters) {
        return stageInvitation.filter(author -> author.getAuthor().equals(filters.getAuthor()));
    }
}
