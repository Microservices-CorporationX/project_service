package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MeetCreatedAtFilterTest {

    private MeetCreatedAtFilter meetCreatedAtFilter;

    private MeetFilterDto filter;
    private Meet meet1;
    private Meet meet2;
    private LocalDateTime filterDate;
    private boolean result;
    private Stream<Meet> meets;
    private List<Meet> filteredMeets;

    @BeforeEach
    void setUp() {
        meetCreatedAtFilter = new MeetCreatedAtFilter();
    }

    @Test
    @DisplayName("Should return true when createdAt filter is not null")
    void isApplicable_whenCreatedAtIsNotNull_shouldReturnTrue() {
        filter = MeetFilterDto.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        result = meetCreatedAtFilter.isApplicable(filter);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when createdAt filter is null")
    void isApplicable_whenCreatedAtIsNull_shouldReturnFalse() {

        filter = MeetFilterDto.builder()
                .createdAt(null)
                .build();
        result = meetCreatedAtFilter.isApplicable(filter);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return matches meets from filter meets by createdAt date")
    void apply_shouldFilterMeetsByCreatedAt() {
        filterDate = LocalDateTime.now().minusDays(2);
        meet1 = Meet.builder().createdAt(LocalDateTime.now().minusDays(1)).build();
        meet2 = Meet.builder().createdAt(LocalDateTime.now().minusDays(3)).build();
        var meet3 = Meet.builder().createdAt(LocalDateTime.now()).build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder()
                .createdAt(filterDate)
                .build();
        filteredMeets = meetCreatedAtFilter.apply(meets, filter).toList();
        assertThat(filteredMeets).hasSize(2);
        assertThat(filteredMeets).contains(meet1, meet3);
        assertThat(filteredMeets).doesNotContain(meet2);
    }

    @Test
    @DisplayName("Should return empty list when no meets match createdAt filter")
    void apply_whenNoMeetsMatchCreatedAtFilter_shouldReturnEmpty() {
        filterDate = LocalDateTime.now();
        meet1 = Meet.builder().createdAt(LocalDateTime.now().minusDays(2)).build();
        meet2 = Meet.builder().createdAt(LocalDateTime.now().minusDays(3)).build();
        meets = Stream.of(meet1, meet2);
        filter = MeetFilterDto.builder()
                .createdAt(filterDate)
                .build();
        filteredMeets = meetCreatedAtFilter.apply(meets, filter).toList();
        assertThat(filteredMeets).isEmpty();
    }
}