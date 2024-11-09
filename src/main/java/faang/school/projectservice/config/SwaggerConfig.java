package faang.school.projectservice.config;

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
                                .url("http://localhost:8082/api/v1/swagger-ui/index.html")
                                .description("Main Server"))
                )
                .info(new Info()
                        .title("Project API")
                        .description("Documentation  for Project Managing")
                        .version("1.0.0"));
    }
}
