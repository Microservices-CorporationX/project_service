package faang.school.projectservice.filter;

import faang.school.projectservice.filter.momentFilter.ProjectPartnerFilter;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectPartnerFilterTest extends SetUpFilterTest {
    ProjectPartnerFilter projectPartnerFilter;

    @BeforeEach

    @Override
    void setUp() {
        super.setUp();
        projectPartnerFilter = new ProjectPartnerFilter();
    }

    @Test
    public void testProjectPartnerFilterisApplicable() {
        assertTrue(projectPartnerFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void testProjectPartnerFilterisNotApplicable() {
        momentFilterDto.setProjectIds(null);
        assertFalse(projectPartnerFilter.isApplicable(momentFilterDto));
    }

    @Test
    void testProjectPartnerFilter() {
        List<Moment> result = projectPartnerFilter.apply(moments, momentFilterDto).toList();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getProjects().get(0).getId());
        assertEquals(3L, result.get(1).getProjects().get(0).getId());
    }
}
