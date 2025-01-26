package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DateFilterTest {
    private static final int invalidMonthNumber = 25;
    private final DateFilter dateFilter = new DateFilter();

    @Test
    void testIsApplicableReturnsTrue() {
        MomentFilterDto filter = MomentFilterDto.builder().monthNumber(Month.JANUARY.getValue()).build();

        assertTrue(dateFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableReturnsFalse() {
        MomentFilterDto filter = MomentFilterDto.builder().monthNumber(invalidMonthNumber).build();

        assertFalse(dateFilter.isApplicable(filter));
    }

    @Test
    void testApply() {
        MomentFilterDto filter = MomentFilterDto.builder().monthNumber(Month.JANUARY.getValue()).build();
        LocalDateTime januaryFirst2024 = LocalDateTime.of(2024, 1, 1, 12, 30);
        LocalDateTime februaryFirst2024 = LocalDateTime.of(2024, 2, 1, 12, 30);

        Moment momentJan = Moment.builder().date(januaryFirst2024).build();
        Moment momentFeb = Moment.builder().date(februaryFirst2024).build();
        Stream<Moment> momentsStream = Stream.of(momentJan, momentFeb);

        List<Moment> filteredMomentList = dateFilter.apply(momentsStream, filter).toList();

        assertTrue(filteredMomentList.contains(momentJan));
        assertFalse(filteredMomentList.contains(momentFeb));
    }
}