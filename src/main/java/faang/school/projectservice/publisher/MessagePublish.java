package faang.school.projectservice.publisher;

public interface MessagePublish<T> {
    void publish(T event);
}
