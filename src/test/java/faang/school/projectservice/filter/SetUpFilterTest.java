package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;


public abstract class SetUpFilterTest {
    protected MomentFilterDto momentFilterDto;
    protected Stream<Moment> moments;

    @BeforeEach
    void setUp() {
        momentFilterDto = MomentFilterDto.builder()
                .month(Month.MARCH)
                .projectIds(List.of(1L, 3L))
                .build();
        Moment moment1 = Moment.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2023, Month.MARCH, 10, 12, 0))
                .projects(List.of(Project.builder().id(1L).build()))
                .build();
        Moment moment2 = Moment.builder()
                .id(2L)
                .createdAt(LocalDateTime.of(2023, Month.APRIL, 10, 12, 0))
                .projects(List.of(Project.builder().id(2L).build()))
                .build();
        Moment moment3 = Moment.builder()
                .id(3L)
                .createdAt(LocalDateTime.of(2023, Month.MARCH, 8, 12, 0))
                .projects(List.of(Project.builder().id(3L).build()))
                .build();
        moments = Stream.of(moment1, moment2, moment3);
    }
}
