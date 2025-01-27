package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MomentDateFilterTest {
    private MomentFilterDto momentFilterDto;
    private final MomentDateFilter filter = new MomentDateFilter();
    private List<Moment> allMoments;

    @BeforeEach
    void setUp() {
        allMoments = TestData.getSomeMoments();
    }

    @Test
    @DisplayName("Test applicability moment filter by Date")
    void testApplicability() {
        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("31/12/2030 00:00:00")
                .dateTo("01/01/1977 00:00:00")
                .build();
        assertFalse(filter.isApplicable(momentFilterDto));

        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("01/01/1977 00:00:00")
                .dateTo("31/12/2030 00:00:00")
                .build();
        assertTrue(filter.isApplicable(momentFilterDto));

        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("01/01/1977 00:00:00")
                .build();
        assertFalse(filter.isApplicable(momentFilterDto));

        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("01/01/1977 00:00:00")
                .build();
        assertFalse(filter.isApplicable(momentFilterDto));

        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("sdfsdfsdf")
                .build();
        assertFalse(filter.isApplicable(momentFilterDto));
    }

    @Test
    @DisplayName("Test applying filter")
    void testApply() {
        momentFilterDto = MomentFilterDto.builder()
                .dateFrom("01/01/1977 00:00:00")
                .dateTo("31/12/2030 00:00:00")
                .build();
        Stream<Moment> momentStream = filter.apply(allMoments.stream(), momentFilterDto);
        List<Moment> filteredMoments = momentStream.toList();
        Assertions.assertEquals(1, filteredMoments.size());
        Assertions.assertEquals(1L, filteredMoments.get(0).getId());
    }
}