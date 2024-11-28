package faang.school.projectservice.filter.meetFilters;

import faang.school.projectservice.model.Meet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TitleFilter implements MeetFilter {

    @Override
    public boolean isApplicable(HttpServletRequest request) {
        return request.getParameter("title") != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> requests, HttpServletRequest request) {
        String title = request.getParameter("title");
        return requests.filter(meet -> meet.getTitle().equals(title));
    }
}
