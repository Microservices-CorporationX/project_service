package faang.school.projectservice.filters.impl;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.filters.abstracts.StageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InvitationDescriptionFilterImpl implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getDescription() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitationStream,
                                         StageInvitationFilterDto filterDto) {
        return invitationStream.filter(stageInvitation ->
                stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED) &&
                        stageInvitation.getDescription().toLowerCase().contains(filterDto.getDescription().toLowerCase()));
    }
}
