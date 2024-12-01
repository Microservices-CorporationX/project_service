package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MomentMonthFilterTest {

    private final MomentMonthFilter momentMonthFilter = new MomentMonthFilter();

    @Test
    void testIsApplicable(){
        MomentFilterDto momentFilterDtoPositive = MomentFilterDto.builder().monthPattern(Month.JANUARY).build();
        MomentFilterDto momentFilterDtoNegative = MomentFilterDto.builder().build();
        assertTrue(momentMonthFilter.isApplicable(momentFilterDtoPositive));
        assertFalse(momentMonthFilter.isApplicable(momentFilterDtoNegative));
    }

    @Test
    void testApply(){
        Moment momentPositive = new Moment();
        momentPositive.setDate(LocalDateTime.of(2023, Month.MARCH, 7, 12, 0));
        Moment momentNegative = new Moment();
        momentNegative.setDate(LocalDateTime.of(2023, Month.JANUARY, 7, 12, 0));
        MomentFilterDto filter = MomentFilterDto.builder().monthPattern(Month.MARCH).build();
        Stream<Moment> result = momentMonthFilter.apply(Stream.of(momentPositive, momentNegative), filter);
        Stream<Moment> expected = Stream.of(momentPositive);
        assertEquals(expected.toList(), result.toList());
    }

}