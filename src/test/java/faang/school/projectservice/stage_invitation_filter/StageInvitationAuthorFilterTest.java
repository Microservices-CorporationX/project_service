package faang.school.projectservice.stage_invitation_filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StageInvitationAuthorFilterTest {

    //TODO НАПИСАТЬ ТЕСТЫ ДЛЯ ВСЕХ ФИЛЬТРОВ

    @InjectMocks
    StageInvitationAuthorFilter stageInvitationAuthorFilter;

    private StageInvitationFilterDto filter;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        StageInvitationFilterDto filter = new StageInvitationFilterDto();
    }

    @Test
    void isApplicable_WhenAuthorIsNull_ReturnsFalse() {
        filter.setAuthor(null);

        boolean result = stageInvitationAuthorFilter.isApplicable(filter);

        assertFalse(result);
    }

//    @Test
//    void isApplicable_WhenAuthorIsNotNull_ReturnsTrue() {
//        StageInvitationFilterDto filter = new StageInvitationFilterDto();
//        filter.setAuthor();
//
//        boolean result = stageInvitationAuthorFilter.isApplicable(filter);
//
//        assertTrue(result);
//    }
//
//    @Test
//    void apply_WhenAuthorMatches_ReturnsFilteredStream() {
//        StageInvitationFilterDto filter = new StageInvitationFilterDto();
//        filter.setAuthor("authorName");
//
//        StageInvitation stage1 = new StageInvitation("authorName");
//        StageInvitation stage2 = new StageInvitation("anotherAuthor");
//
//        Stream<StageInvitation> invitations = Stream.of(stage1, stage2);
//
//        Stream<StageInvitation> result = stageInvitationAuthorFilter.apply(invitations, filter);
//
//        assertTrue(result.allMatch(stageInvitation -> "authorName".equals(stageInvitation.getAuthor())));
//    }
//
//    @Test
//    void apply_WhenAuthorDoesNotMatch_ReturnsEmptyStream() {
//        StageInvitationFilterDto filter = new StageInvitationFilterDto();
//        filter.setAuthor("nonMatchingAuthor");
//
//        StageInvitation stage1 = new StageInvitation("authorName");
//        StageInvitation stage2 = new StageInvitation("anotherAuthor");
//
//        Stream<StageInvitation> invitations = Stream.of(stage1, stage2);
//
//        Stream<StageInvitation> result = stageInvitationAuthorFilter.apply(invitations, filter);
//
//        assertFalse(result.anyMatch(stageInvitation -> "authorName".equals(stageInvitation.getAuthor())));
//    }

}
