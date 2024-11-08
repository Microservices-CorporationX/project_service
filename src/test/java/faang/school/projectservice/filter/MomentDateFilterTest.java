package faang.school.projectservice.filter;

import faang.school.projectservice.filter.momentFilter.MomentDateFilter;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MomentDateFilterTest extends SetUpFilterTest {
    private MomentDateFilter momentDateFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        momentDateFilter = new MomentDateFilter();
    }

    @Test
    public void testMomentDateFilterisApplicable() {
        assertTrue(momentDateFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void testMomentDateFilterNotApplicable() {
        momentFilterDto.setMonth(null);
        assertFalse(momentDateFilter.isApplicable(momentFilterDto));
    }

    @Test
    void testMomentDateFilter() {
        List<Moment> filteredMoments = momentDateFilter.applay(moments, momentFilterDto).toList();

        assertEquals(2, filteredMoments.size());
        assertEquals(Month.MARCH, filteredMoments.get(0).getCreatedAt().getMonth());
        assertEquals(Month.MARCH, filteredMoments.get(1).getCreatedAt().getMonth());
    }
}
