package faang.school.projectservice.publisher;

public interface RedisMessagePublisher {
    void publish(Long fundRaisedId);
}
