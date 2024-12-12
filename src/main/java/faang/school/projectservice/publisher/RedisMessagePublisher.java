package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.FundRaisedEvent;

public interface RedisMessagePublisher {
    void publish(FundRaisedEvent event);
}
