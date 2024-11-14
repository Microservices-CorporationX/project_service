package faang.school.projectservice.service.invitation.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

@AllArgsConstructor
public class DateFilter implements InvitationFilter {
    private final LocalDate date;

    @Override
    public boolean matches(StageInvitation invitation) {
        return invitation.getCreatedDate().toLocalDate().equals(date);
    }
}
