package faang.school.projectservice.filters.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class MomentFilterTest {
    private final MomentFilterToFromDate momentFilterToFromDate = new MomentFilterToFromDate();

    @ParameterizedTest
    @MethodSource("provideFilterDtoTestCases")
    void testIsApplicable(MomentFilterDto filterDto, boolean expected) {
        boolean result = momentFilterToFromDate.isApplicable(filterDto);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> provideFilterDtoTestCases() {
        return Stream.of(
                Arguments.of(MomentFilterDto.builder()
                        .fromDate(null)
                        .toDate(null)
                        .build(), false),

                Arguments.of(MomentFilterDto.builder()
                        .fromDate(null)
                        .toDate(LocalDateTime.now())
                        .build(), false),

                Arguments.of(MomentFilterDto.builder()
                        .fromDate(LocalDateTime.now())
                        .toDate(null)
                        .build(), false),

                Arguments.of(MomentFilterDto.builder()
                        .fromDate(LocalDateTime.now().minusDays(1))
                        .toDate(LocalDateTime.now().plusDays(1))
                        .build(), true)
        );
    }

    @Test
    void testApply() {
        MomentMapperImpl momentMapper = new MomentMapperImpl();
        Moment moment1 = momentMapper.toEntity(MomentDto.builder().name("Moment 1").date(LocalDateTime.now().minusDays(2)).build());
        Moment moment2 = momentMapper.toEntity(MomentDto.builder().name("Moment 2").date(LocalDateTime.now().minusHours(10)).build());
        Moment moment3 = momentMapper.toEntity(MomentDto.builder().name("Moment 3").date(LocalDateTime.now().plusHours(10)).build());
        Moment moment4 = momentMapper.toEntity(MomentDto.builder().name("Moment 4").date(LocalDateTime.now().plusDays(2)).build());

        List<Moment> moments = List.of(moment1, moment2, moment3, moment4);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
        LocalDateTime toDate = LocalDateTime.now().plusDays(1);
        MomentFilterDto filterDto = MomentFilterDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        List<Moment> filteredResult = momentFilterToFromDate.apply(moments.stream(), filterDto).toList();

        Assertions.assertEquals(2, filteredResult.size());
        Assertions.assertEquals("Moment 2", filteredResult.get(0).getName());
        Assertions.assertEquals("Moment 3", filteredResult.get(1).getName());
    }
}
