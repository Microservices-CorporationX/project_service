package faang.school.projectservice.exception.webclient;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.MessageError;
import faang.school.projectservice.exception.ServiceCallException;
import faang.school.projectservice.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class WebClientErrorHandler {

    private static final Map<HttpStatus, Function<String, RuntimeException>> STATUS_TO_EXCEPTION_HANDLER = new HashMap<>();

    static {
        STATUS_TO_EXCEPTION_HANDLER.put(
                HttpStatus.BAD_REQUEST,
                (message) -> new DataValidationException(MessageError.DATA_VALIDATION_EXCEPTION.getMessage(message))
        );
        STATUS_TO_EXCEPTION_HANDLER.put(
                HttpStatus.UNAUTHORIZED,
                (message) -> new UnauthorizedException(MessageError.USER_UNAUTHORIZED_EXCEPTION.getMessage(message))
        );
        STATUS_TO_EXCEPTION_HANDLER.put(
                HttpStatus.FORBIDDEN,
                (message) -> new ForbiddenException(MessageError.FORBIDDEN_EXCEPTION.getMessage(message))
        );
    }

    public <T> Mono<T> handleResponse(ClientResponse response, Class<T> responseType) {
        HttpStatus status = HttpStatus.valueOf(response.statusCode().value());

        if (status.is2xxSuccessful()) {
            return response.bodyToMono(responseType);
        }

        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(handleErrorResponse(status, errorBody)));
    }

    private RuntimeException handleErrorResponse(HttpStatus status, String message) {
        return STATUS_TO_EXCEPTION_HANDLER.getOrDefault(
                status,
                (exMessage) -> new ServiceCallException(MessageError.EXTERNAL_SERVICE_UNEXPECTED_EXCEPTION.getMessage("Jira exception."))
        ).apply(message);
    }
}
