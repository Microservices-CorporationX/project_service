package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MomentProjectFilterTest {

    private final MomentProjectFilter momentProjectFilter = new MomentProjectFilter();

    @Test
    void testIsApplicable(){
        MomentFilterDto momentFilterDtoPositive = MomentFilterDto.builder().projectIds(List.of(1L, 3L)).build();
        MomentFilterDto momentFilterDtoNegative = MomentFilterDto.builder().build();
        assertTrue(momentProjectFilter.isApplicable(momentFilterDtoPositive));
        assertFalse(momentProjectFilter.isApplicable(momentFilterDtoNegative));
    }

    @Test
    void testApply(){
        Moment moment1 = Moment.builder()
                .id(1L)
                .projects(List.of(Project.builder().id(1L).build()))
                .build();
        Moment moment2 = Moment.builder()
                .id(2L)
                .projects(List.of(Project.builder().id(2L).build()))
                .build();
        Moment moment3 = Moment.builder()
                .id(3L)
                .projects(List.of(Project.builder().id(3L).build()))
                .build();
        MomentFilterDto filter = MomentFilterDto.builder().projectIds(List.of(1L, 3L)).build();
        Stream<Moment> result = momentProjectFilter.apply(Stream.of(moment1, moment2, moment3), filter);
        Stream<Moment> expected = Stream.of(moment1, moment3);
        assertEquals(expected.toList(), result.toList());
    }


}