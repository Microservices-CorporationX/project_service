package faang.school.projectservice.filter;

import faang.school.projectservice.filter.meetFilters.DateFilter;
import faang.school.projectservice.filter.meetFilters.MeetFilter;
import faang.school.projectservice.filter.meetFilters.TitleFilter;
import faang.school.projectservice.model.Meet;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MeetFilterTest {

    private MeetFilter filter;
    private Meet firstMeet;
    private Meet secondMeet;
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);


    @BeforeEach
    public void setUp() {
        firstMeet = new Meet();
        firstMeet.setId(1L);
        firstMeet.setCreatedAt(LocalDateTime.now());
        firstMeet.setTitle("title");
        secondMeet = new Meet();
        secondMeet.setId(2L);
        secondMeet.setCreatedAt(LocalDateTime.MAX);
        secondMeet.setTitle("title2");
    }

    @Test
    public void testTitleFilter() {
        filter = new TitleFilter();
        when(request.getParameter("title")).thenReturn("title");

        assertTrue(filter.isApplicable(request));
        Stream<Meet> meetStream = filter.apply(Stream.of(firstMeet, secondMeet), request);
        assertTrue(meetStream.allMatch(meet -> meet.getTitle().equals("title")));
    }

    @Test
    public void testDateFilter() {
        filter = new DateFilter();
        when(request.getParameter("date")).thenReturn(LocalDate.now().toString());

        assertTrue(filter.isApplicable(request));
        Stream<Meet> meetStream = filter.apply(Stream.of(firstMeet, secondMeet), request);
        assertTrue(meetStream.allMatch(meet -> meet.getCreatedAt().toLocalDate().equals(LocalDate.now())));
    }
}
