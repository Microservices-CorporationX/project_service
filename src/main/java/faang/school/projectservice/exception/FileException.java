package faang.school.projectservice.exception;

public class FileException extends RuntimeException {
    public FileException(String errorUploadingFileToS3) {
        super(errorUploadingFileToS3);
    }
}
