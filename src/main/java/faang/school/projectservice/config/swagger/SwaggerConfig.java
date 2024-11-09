package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(
                        List.of(new Server()
                                .url("http://localhost:8082/api/v1/")
                                .description("Main Server"))
                )
                .info(new Info()
                        .title("Project API")
                        .version("1.0.0")
                        .description("Documentation  for Project Managing"));
    }
}
