package faang.school.projectservice.filter;


import java.util.stream.Stream;

public interface SubProjectFilter<T, F> {
    boolean isApplicable(T filter);

    Stream<F> apply(Stream<F> itemStream, T filter);
}
