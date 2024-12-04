package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetDto;
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
public class MeetNameFilterTest {
    private final MeetNameFilter meetNameFilter = new MeetNameFilter();
    private  MeetDto meetDto;
    private Meet meet1;
    private Meet meet2;
    private Meet meet3;

    @BeforeEach()
    void setUp() {
       meetDto = MeetDto.builder()
                .title("Meeting 1")
                .build();

        meet1 = new Meet();
        meet1.setTitle("Meeting 1");

        meet2 = new Meet();
        meet2.setTitle("Meeting 2");

        meet3 = new Meet();
        meet3.setTitle("Meeting 3");

    }

    @Test
    void testIsApplicable_WithNonNullTitle() {
        assertTrue(meetNameFilter.isApplicable(meetDto));
    }

    @Test
    void testIsApplicable_WithNullTitle() {
        meetDto.setTitle(null);
        assertFalse(meetNameFilter.isApplicable(meetDto));
    }

    @Test
    void testApply_WithMatchingTitle() {
        Stream<Meet> meetStream = Stream.of(meet1, meet2, meet3);

        Stream<Meet> filteredStream = meetNameFilter.apply(meetStream, meetDto);

        assertEquals(1, filteredStream.count());
    }

    @Test
    void testApply_WithNoMatchingTitle() {
        meetDto.setTitle("Non-existent Title");

        Stream<Meet> meetStream = Stream.of(meet1, meet2, meet3);

        Stream<Meet> filteredStream = meetNameFilter.apply(meetStream, meetDto);

        assertEquals(0, filteredStream.count());
    }

    @Test
    void testApply_WithMultipleMatches() {
        meetDto.setTitle("Meeting");

        Stream<Meet> meetStream = Stream.of(meet1, meet2, meet3);

        Stream<Meet> filteredStream = meetNameFilter.apply(meetStream, meetDto);

        assertEquals(3, filteredStream.count());
    }

    @Test
    void testApply_WithEmptyStream() {
        Stream<Meet> meetStream = Stream.empty();

        Stream<Meet> filteredStream = meetNameFilter.apply(meetStream, meetDto);

        assertEquals(0, filteredStream.count());
    }
}
