package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MeetDateFilterTest {
    private MeetDateFilter meetDateFilter;
    private MeetDto meetDto;
    private Meet meet1;
    private Meet meet2;

    @BeforeEach
    void setUp() {
        meetDateFilter = new MeetDateFilter();
        meetDto = MeetDto.builder()
                .createdAt(LocalDateTime.of(2024, 12, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 12, 2, 0, 0))
                .build();

        meet1 = new Meet();
        meet1.setCreatedAt(LocalDateTime.of(2024, 12, 1, 5, 0));

        meet2 = new Meet();
        meet2.setCreatedAt(LocalDateTime.of(2024, 12, 3, 5, 0));
    }

    @Test
    void testIsApplicable_WithValidDates() {
        assertTrue(meetDateFilter.isApplicable(meetDto));
    }

    @Test
    void testIsApplicable_WithNullCreatedAt() {
        meetDto.setCreatedAt(null);
        assertFalse(meetDateFilter.isApplicable(meetDto));
    }

    @Test
    void testIsApplicable_WithNullUpdatedAt() {
        meetDto.setUpdatedAt(null);
        assertFalse(meetDateFilter.isApplicable(meetDto));
    }

    @Test
    void testApply_WithValidDateRange() {
        Stream<Meet> meetStream = Stream.of(meet1, meet2);

        Stream<Meet> filteredStream = meetDateFilter.apply(meetStream, meetDto);

        assertEquals(1, filteredStream.count());
    }

    @Test
    void testApply_WithNoMatches() {
        meetDto.setCreatedAt(LocalDateTime.of(2024, 12, 1, 0, 0));
        meetDto.setUpdatedAt(LocalDateTime.of(2024, 12, 1, 0, 0));

        Stream<Meet> meetStream = Stream.of(meet1, meet2);

        Stream<Meet> filteredStream = meetDateFilter.apply(meetStream, meetDto);

        assertEquals(0, filteredStream.count());
    }

    @Test
    void testApply_WithEmptyStream() {
        Stream<Meet> meetStream = Stream.empty();

        Stream<Meet> filteredStream = meetDateFilter.apply(meetStream, meetDto);

        assertEquals(0, filteredStream.count());
    }
}
