package faang.school.projectservice.filter.project.meet;

import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MeetTitleFilterTest {

    private MeetTitleFilter meetTitleFilter;

    private MeetFilterDto meetFilterDto;

    @BeforeEach
    public void setUp() {
        meetTitleFilter = new MeetTitleFilter();
        meetFilterDto = new MeetFilterDto();

        meetFilterDto.setTitle("title");
    }

    @Test
    public void testIsApplicableFailed() {
        meetFilterDto.setTitle(null);
        assertFalse(meetTitleFilter.isApplicable(meetFilterDto));
    }

    @Test
    public void testIsApplicableSuccessful() {
        assertTrue(meetTitleFilter.isApplicable(meetFilterDto));
    }

    @Test
    public void testApply() {
        Meet firstMeet = new Meet();
        firstMeet.setTitle("Title");

        Meet secondMeet = new Meet();
        secondMeet.setTitle("name");

        Stream<Meet> meetStream = Stream.of(firstMeet, secondMeet);
        Stream<Meet> result = meetTitleFilter.apply(meetFilterDto, meetStream);

        assertEquals(1, result.count());
    }

}
