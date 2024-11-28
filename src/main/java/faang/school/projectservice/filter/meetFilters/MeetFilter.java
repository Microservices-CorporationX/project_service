package faang.school.projectservice.filter.meetFilters;

import faang.school.projectservice.model.Meet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.stream.Stream;

public interface MeetFilter {
    boolean isApplicable(HttpServletRequest request);

    Stream<Meet> apply(Stream<Meet> requests, HttpServletRequest request);
}
