package faang.school.projectservice.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TikaConfig {

    @Bean
    public Tika tika() {
        return new Tika();
    }
}