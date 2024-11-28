package faang.school.projectservice.filter.project.meet;

import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.dto.project.meet.util.RangeDateTime;
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
public class MeetRangeDateTimeTest {

    private MeetRangeDateTimeFilter meetRangeDateTimeFilter;

    private MeetFilterDto meetFilterDto;

    @BeforeEach
    public void setUp() {
        meetRangeDateTimeFilter = new MeetRangeDateTimeFilter();
        meetFilterDto = new MeetFilterDto();

        RangeDateTime rangeDateTime = new RangeDateTime();
        rangeDateTime.setStart(LocalDateTime.now().minusDays(1));
        rangeDateTime.setEnd(LocalDateTime.now().plusDays(1));

        meetFilterDto.setRangeDateTime(rangeDateTime);
    }

    @Test
    public void testIsApplicableFailed() {
        meetFilterDto.setRangeDateTime(null);
        assertFalse(meetRangeDateTimeFilter.isApplicable(meetFilterDto));
    }

    @Test
    public void testIsApplicableSuccessful() {
        assertTrue(meetRangeDateTimeFilter.isApplicable(meetFilterDto));
    }

    @Test
    public void testApply() {
        Meet firstMeet = new Meet();
        firstMeet.setStartDateTime(LocalDateTime.now());

        Meet secondMeet = new Meet();
        secondMeet.setStartDateTime(LocalDateTime.now().minusDays(2));

        Stream<Meet> meetStream = Stream.of(firstMeet, secondMeet);
        Stream<Meet> result = meetRangeDateTimeFilter.apply(meetFilterDto, meetStream);
        assertEquals(1, result.count());
    }
}
