package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<S, F> {
    boolean isApplicable(F filter);

    Stream<S> apply(Stream<S> elements, F filter);
}
