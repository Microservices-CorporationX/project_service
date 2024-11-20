package faang.school.projectservice.filter.meetFilters;

import faang.school.projectservice.model.Meet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Stream;

@Component
public class DateFilter implements MeetFilter {

    @Override
    public boolean isApplicable(HttpServletRequest request) {
        return request.getParameter("date") != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> requests, HttpServletRequest request) {
        LocalDate ld = LocalDate.parse(request.getParameter("date"));
        return requests.filter(meet -> meet.getCreatedAt().toLocalDate().equals(ld));
    }
}
