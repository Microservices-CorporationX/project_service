package ru.corporationx.projectservice.publisher.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.team.TeamEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamEventPublisher  {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.team-channel.name}")
    private String teamChannel;

    public void publish(TeamEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(teamChannel, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
