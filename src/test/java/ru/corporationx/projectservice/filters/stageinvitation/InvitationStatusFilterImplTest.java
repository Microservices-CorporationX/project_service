package ru.corporationx.projectservice.filters.stageinvitation;

import ru.corporationx.projectservice.model.dto.invitation.StageInvitationFilterDto;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Test;
import ru.corporationx.projectservice.filters.stageinvitation.InvitationStatusFilterImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationStatusFilterImplTest {
    private final InvitationStatusFilterImpl statusFilter = new InvitationStatusFilterImpl();

    @Test
    public void testIsApplicable() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        boolean isApplicable = statusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        boolean isApplicable = statusFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyStatusFilter() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .status(StageInvitationStatus.ACCEPTED)
                .build();
        StageInvitation firstInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.ACCEPTED)
                .build();
        StageInvitation secondInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.REJECTED)
                .build();
        Stream<StageInvitation> invitationStream = Stream.of(firstInvitation, secondInvitation);

        List<StageInvitation> resultList = statusFilter.apply(invitationStream, filterDto).toList();

        assertEquals(1, resultList.size());
        assertEquals(StageInvitationStatus.ACCEPTED, resultList.get(0).getStatus());
    }
}