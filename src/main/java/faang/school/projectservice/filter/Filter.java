package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<D, F> {
    boolean isApplicable(F filter);

    Stream<D> applay(Stream<D> moments, F filter);
}
