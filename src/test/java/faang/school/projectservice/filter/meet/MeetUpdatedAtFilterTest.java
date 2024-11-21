package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MeetUpdatedAtFilterTest {
    private MeetUpdatedAtFilter meetUpdatedAtFilter;

    private MeetFilterDto filter;
    private boolean result;
    private LocalDateTime filterDate;
    private Meet meet1;
    private Meet meet2;
    private Stream<Meet> meets;
    private List<Meet> filteredMeets;

    @BeforeEach
    void setUp() {
        meetUpdatedAtFilter = new MeetUpdatedAtFilter();
    }

    @Test
    @DisplayName("Should return true when updatedAt filter is present")
    void shouldReturnTrueWhenUpdatedAtFilterIsPresent() {
        filter = MeetFilterDto.builder()
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        result = meetUpdatedAtFilter.isApplicable(filter);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when updatedAt filter is missing")
    void shouldReturnFalseWhenUpdatedAtFilterIsMissing() {
        filter = MeetFilterDto.builder()
                .updatedAt(null)
                .build();
        result = meetUpdatedAtFilter.isApplicable(filter);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return meets updated before specified date")
    void shouldReturnMeetsUpdatedBeforeSpecifiedDate() {
        filterDate = LocalDateTime.now().minusDays(2);
        meet1 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(1)).build();
        meet2 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(3)).build();
        var meet3 = Meet.builder().updatedAt(LocalDateTime.now()).build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder()
                .updatedAt(filterDate)
                .build();
        filteredMeets = meetUpdatedAtFilter.apply(meets, filter).toList();
        assertThat(filteredMeets).hasSize(1);
        assertThat(filteredMeets).contains(meet2);
        assertThat(filteredMeets).doesNotContain(meet1, meet3);
    }

    @Test
    @DisplayName("Should return empty list when no meets match the update date criteria")
    void shouldReturnEmptyListWhenNoMeetsMatchUpdateDateCriteria() {
        filterDate = LocalDateTime.now().minusDays(5);
        meet1 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(1)).build();
        meet2 = Meet.builder().updatedAt(LocalDateTime.now().minusDays(3)).build();
        meets = Stream.of(meet1, meet2);
        filter = MeetFilterDto.builder()
                .updatedAt(filterDate)
                .build();
        filteredMeets = meetUpdatedAtFilter.apply(meets, filter).toList();
        assertThat(filteredMeets).isEmpty();
    }
}