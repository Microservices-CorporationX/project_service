package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaskKeywordFilterTest {

    private TaskKeywordFilter taskKeywordFilter;
    private Task firstTask;
    private Task secondTask;
    private Task thirdTask;
    private List<Task> tasks;

    @BeforeEach
    public void setUp() {
        taskKeywordFilter = new TaskKeywordFilter();

        firstTask = Task.builder()
                .description("this is some description")
                .name("first task")
                .build();
        secondTask = Task.builder()
                .description("this is another description")
                .name("second task")
                .build();
        thirdTask = Task.builder()
                .description("blank")
                .name("third task")
                .build();
        tasks = List.of(firstTask, secondTask, thirdTask);
    }

    @Test
    public void testIsApplicable() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .keyword("is")
                .build();
        boolean expected = true;

        // act
        boolean actual = taskKeywordFilter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNotApplicable() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder().build();
        boolean expected = false;

        // act
        boolean actual = taskKeywordFilter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApply() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .keyword("is")
                .build();

        List<Task> expected = List.of(firstTask, secondTask);

        // act
        List<Task> actual = taskKeywordFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyNoMatches() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .keyword("java")
                .build();

        List<Task> expected = new ArrayList<>();

        // act
        List<Task> actual = taskKeywordFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyEmptyTasks() {
        // arrange
        List<Task> tasks = new ArrayList<>();
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .keyword("is")
                .build();

        List<Task> expected = new ArrayList<>();

        // act
        List<Task> actual = taskKeywordFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }
}
