package faang.school.projectservice.filters.impl;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationAuthorFilterImplTest {
    private static final Long AUTHOR_ID = 1L;

    private final InvitationAuthorFilterImpl authorFilter = new InvitationAuthorFilterImpl();

    @Test
    public void testIsApplicable() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .authorId(1L)
                .build();

        boolean isApplicable = authorFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testIsNotApplicable() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        boolean isApplicable = authorFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testApplyAuthorFilter() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .authorId(AUTHOR_ID)
                .build();
        TeamMember firstMember = TeamMember.builder()
                .id(AUTHOR_ID)
                .build();
        TeamMember secondMember = TeamMember.builder()
                .id(AUTHOR_ID + 1)
                .build();
        StageInvitation firstInvitation = StageInvitation.builder()
                .author(firstMember)
                .build();
        StageInvitation secondInvitation = StageInvitation.builder()
                .author(secondMember)
                .build();
        Stream<StageInvitation> invitationStream = Stream.of(firstInvitation, secondInvitation);

        List<StageInvitation> resultList = authorFilter.apply(invitationStream, filterDto).toList();

        assertEquals(1, resultList.size());
        assertEquals(AUTHOR_ID, resultList.get(0).getAuthor().getId());
    }
}