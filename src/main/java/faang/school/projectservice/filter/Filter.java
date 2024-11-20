package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter <D, F> {

    boolean isApplicable(F filter);

    Stream<D> apply(Stream<D> stream, F filter);
}
