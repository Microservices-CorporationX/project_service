package ru.corporationx.projectservice.exception;

public class StorageSizeExceededException extends RuntimeException {
    public StorageSizeExceededException(String message) {
        super(message);
    }
}
