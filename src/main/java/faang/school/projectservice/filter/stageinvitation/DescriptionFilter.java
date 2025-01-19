package faang.school.projectservice.filter.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        String description = filter.getDescriptionPattern();
        return filter.getDescriptionPattern() != null && !description.isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> events, StageInvitationFilterDto filter) {
        return events.filter(event -> event.getDescription().toLowerCase()
                .contains(filter.getDescriptionPattern().toLowerCase()));
    }
}
