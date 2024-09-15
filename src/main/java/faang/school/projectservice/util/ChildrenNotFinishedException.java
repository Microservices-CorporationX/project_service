package faang.school.projectservice.util;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
public class ChildrenNotFinishedException extends Exception{
    public final String message;

    public ChildrenNotFinishedException(String message) {
        super(message);
        this.message = message;
    }
}
