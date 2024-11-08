package faang.school.projectservice.filter;


import java.util.stream.Stream;

public interface Filter<T, F> {
    boolean isApplicable(T filter);

    Stream<F> apply(Stream<F> itemStream, T filter);
}
