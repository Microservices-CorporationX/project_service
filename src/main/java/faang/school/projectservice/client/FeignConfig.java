package faang.school.projectservice.client;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.feignclient.FeignErrorDecoderBase;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoderBase();
    }
}
