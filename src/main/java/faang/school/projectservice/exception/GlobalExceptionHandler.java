package faang.school.projectservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        return buildResponse(e);
    }

    @ExceptionHandler(StorageExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleStorageExceededException(StorageExceededException e) {
        return buildResponse(e);
    }

    @ExceptionHandler({ZippingFileError.class, StreamingFileError.class,
            FileDownloadException.class, FileUploadException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleZipFileStreamingFileFileDownloadFileUploadExceptions(Exception e) {
        return buildResponse(e);
    }

    private ErrorResponse buildResponse(Exception e) {
        log.error(e.getClass().getSimpleName(), e);
        return ErrorResponse.builder()
                .error(e.getClass().getSimpleName())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
