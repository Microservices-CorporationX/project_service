package faang.school.projectservice.filter;

public interface Filter<T, U> {
    boolean isApplicable(T t);
    U apply(T t, U u);
}
