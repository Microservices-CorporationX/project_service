package faang.school.projectservice.exception.webclient;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.ServiceCallException;
import faang.school.projectservice.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebClientErrorHandlerTest {

    @Mock
    ClientResponse clientResponse;

    @InjectMocks
    private WebClientErrorHandler webClientErrorHandler;

    @Test
    void handleBadRequestResponse() {
        String errorMessage = "Invalid request";

        when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorMessage));

        Mono<Void> result = webClientErrorHandler.handleResponse(clientResponse, Void.class);

        assertThrows(DataValidationException.class, result::block);
    }

    @Test
    void handleUnauthorizedException() {
        String errorMessage = "Unauthorized";

        when(clientResponse.statusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorMessage));

        Mono<Void> result = webClientErrorHandler.handleResponse(clientResponse, Void.class);

        assertThrows(UnauthorizedException.class, result::block);
    }

    @Test
    void handleForbiddenException() {
        String errorMessage = "Forbidden";

        when(clientResponse.statusCode()).thenReturn(HttpStatus.FORBIDDEN);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorMessage));

        Mono<Void> result = webClientErrorHandler.handleResponse(clientResponse, Void.class);

        assertThrows(ForbiddenException.class, result::block);
    }

    @Test
    void handleWebclientException() {
        String errorMessage = "Webclient exception";

        when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorMessage));

        Mono<Void> result = webClientErrorHandler.handleResponse(clientResponse, Void.class);

        assertThrows(ServiceCallException.class, result::block);
    }

    @Test
    void handleSuccessResponse() {
        String responseBody = "Success";

        when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(responseBody));

        Mono<String> result = webClientErrorHandler.handleResponse(clientResponse, String.class);

        assertEquals(responseBody, result.block());
    }
}