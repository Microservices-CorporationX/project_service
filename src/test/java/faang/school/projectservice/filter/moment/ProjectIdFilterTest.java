package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectIdFilterTest {
    private static final long matchProjectId = 1L;
    private static final long nonMatchProjectId = 0L;
    private final ProjectIdFilter projectIdFilter = new ProjectIdFilter();

    @Test
    void testIsApplicableReturnsTrue() {
        MomentFilterDto filter = MomentFilterDto.builder().projectId(matchProjectId).build();

        assertTrue(projectIdFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableReturnsFalse() {
        MomentFilterDto filter = MomentFilterDto.builder().projectId(nonMatchProjectId).build();

        assertFalse(projectIdFilter.isApplicable(filter));
    }

    @Test
    void testApply() {
        MomentFilterDto filter = MomentFilterDto.builder().projectId(matchProjectId).build();
        Project matchingProject = Project.builder().id(matchProjectId).build();
        Project nonMatchingProject = Project.builder().id(nonMatchProjectId).build();

        Moment momentWithMatchingProject = Moment.builder().projects(List.of(matchingProject)).build();
        Moment momentWithNonMatchingProject = Moment.builder().projects(List.of(nonMatchingProject)).build();
        Stream<Moment> momentsStream = Stream.of(momentWithMatchingProject, momentWithNonMatchingProject);

        List<Moment> filteredMomentList = projectIdFilter.apply(momentsStream, filter).toList();

        assertTrue(filteredMomentList.contains(momentWithMatchingProject));
        assertFalse(filteredMomentList.contains(momentWithNonMatchingProject));
    }
}