package faang.school.projectservice.filter;

import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> allStageInvitations,
            StageInvitationFilterDto stageInvitationFilterDto);
}
