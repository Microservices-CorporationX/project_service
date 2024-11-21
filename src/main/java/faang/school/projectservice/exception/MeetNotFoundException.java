package faang.school.projectservice.exception;

public class MeetNotFoundException extends RuntimeException {
  public MeetNotFoundException(Long meetId) {
    super("Meet not found with ID: " + meetId);
  }
}