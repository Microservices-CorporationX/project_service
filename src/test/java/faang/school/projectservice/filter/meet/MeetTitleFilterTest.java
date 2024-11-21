package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MeetTitleFilterTest {
    private MeetTitleFilter meetTitleFilter;

    private MeetFilterDto filter;
    private boolean result;
    private Meet meet1;
    private Meet meet2;
    private Meet meet3;
    private Stream<Meet> meets;
    private List<Meet> filteredMeets;

    @BeforeEach
    void setUp() {
        meetTitleFilter = new MeetTitleFilter();
    }

    @Test
    @DisplayName("Should return true when title pattern is provided")
    void shouldReturnTrueWhenTitlePatternIsProvided() {
        filter = MeetFilterDto.builder().titlePattern("meeting").build();
        result = meetTitleFilter.isApplicable(filter);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when title pattern is null")
    void shouldReturnFalseWhenTitlePatternIsNull() {
        filter = MeetFilterDto.builder().titlePattern(null).build();
        result = meetTitleFilter.isApplicable(filter);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should filter meetings by matching title pattern")
    void shouldFilterMeetingsByMatchingTitlePattern(    ) {
        meet1 = Meet.builder().title("Team Meeting").build();
        meet2 = Meet.builder().title("Project Discussion").build();
        meet3 = Meet.builder().title("Team Standup").build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder().titlePattern("team").build();
        filteredMeets = meetTitleFilter.apply(meets, filter).toList();
        assertThat(filteredMeets).hasSize(2);
        assertThat(filteredMeets).contains(meet1, meet3);
        assertThat(filteredMeets).doesNotContain(meet2);
    }

    @Test
    @DisplayName("Should return empty list when no titles match pattern")
    void shouldReturnEmptyListWhenNoTitlesMatchPattern() {
        meet1 = Meet.builder().title("Team Meeting").build();
        meet2 = Meet.builder().title("Project Discussion").build();
        meet3 = Meet.builder().title("Team Standup").build();
        meets = Stream.of(meet1, meet2, meet3);
        filter = MeetFilterDto.builder().titlePattern("workshop").build();
        filteredMeets = meetTitleFilter.apply(meets, filter).toList();
        assertThat(filteredMeets).isEmpty();
    }
}