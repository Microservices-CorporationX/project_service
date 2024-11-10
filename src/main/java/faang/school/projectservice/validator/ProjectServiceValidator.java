package faang.school.projectservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProjectServiceValidator {
    public boolean isMentorPresent(List<Long> memberIds, long mentorId) {
        return memberIds.stream().anyMatch(id -> id == mentorId);
    }
}
