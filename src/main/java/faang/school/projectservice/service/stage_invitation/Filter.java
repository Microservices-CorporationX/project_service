package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.client.StageInvitationFilters;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface Filter {
    boolean isApplicable(StageInvitationFilters filters);

    Stream<StageInvitation> apply(Stream<StageInvitation> data, StageInvitationFilters filters);
}
