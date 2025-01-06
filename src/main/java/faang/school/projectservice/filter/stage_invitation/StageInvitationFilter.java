package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto filters);

    Stream<StageInvitation> apply (Stream<StageInvitation> stageInvitation, StageInvitationFilterDto filters);

}
