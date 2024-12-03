package faang.school.projectservice.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API Project Service")
                                .version("1.0")
                                .description("API documentation for Project Service")
                                .summary("This service is working for all projects")
                );
    }

    @Bean
    public OperationCustomizer addGlobalHeaders() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.addParametersItem(new Parameter()
                    .in("header")
                    .schema(new StringSchema())
                    .name("x-user-id")
                    .description("User ID")
                    .required(true));
            return operation;
        };
    }
}
