package faang.school.projectservice.filter.stageinvitationfilter;

import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stageinvitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto filter);

    Stream<StageInvitation> apply(Stream<StageInvitation> invitations, StageInvitationFilterDto filter);
}
