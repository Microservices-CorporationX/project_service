package faang.school.projectservice.exception.feignclient;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.MessageError;
import faang.school.projectservice.exception.ServiceCallException;
import faang.school.projectservice.exception.UnauthorizedException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FeignErrorDecoderBase implements ErrorDecoder {

    private static final int BAD_REQUEST_CODE = 404;
    private static final int UNAUTHORIZED_CODE = 401;
    private static final int FORBIDDEN_CODE = 403;
    private static final Map<Integer, Function<String, RuntimeException>> STATUS_TO_EXCEPTION_HANDLER = new HashMap<>();

    static {
        STATUS_TO_EXCEPTION_HANDLER.put(
                BAD_REQUEST_CODE,
                (message) -> new DataValidationException(MessageError.DATA_VALIDATION_EXCEPTION.getMessage(message))
        );
        STATUS_TO_EXCEPTION_HANDLER.put(
                UNAUTHORIZED_CODE,
                (message) -> new UnauthorizedException(MessageError.USER_UNAUTHORIZED_EXCEPTION.getMessage(message))
        );
        STATUS_TO_EXCEPTION_HANDLER.put(
                FORBIDDEN_CODE,
                (message) -> new DataValidationException(MessageError.FORBIDDEN_EXCEPTION.getMessage(message))
        );
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        int statusCode = response.status();
        return handleErrorResponse(statusCode, response.body().toString());
    }

    private RuntimeException handleErrorResponse(int statusCode, String message) {
        return STATUS_TO_EXCEPTION_HANDLER.getOrDefault(
                statusCode,
                (exMessage) -> new ServiceCallException(MessageError.EXTERNAL_SERVICE_UNEXPECTED_EXCEPTION.getMessage("Jira exception"))
        ).apply(message);
    }
}
