package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<T, F> {
    boolean isApplicable(F filter);

    Stream<T> apply(Stream<T> events, F filter);
}
